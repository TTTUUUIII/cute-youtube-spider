package com.whitesky.youtube.cutespider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.istack.internal.NotNull;
import com.whitesky.youtube.deserializer.SearchResponseContextDeserializer;
import com.whitesky.youtube.deserializer.SuggestResponseContextDeserializer;
import com.whitesky.youtube.deserializer.WatchResponseContextDeserializer;
import com.whitesky.youtube.responsecontext.SearchResponseContext;
import com.whitesky.youtube.responsecontext.SuggestResponseContext;
import com.whitesky.youtube.responsecontext.WatchResponseContext;
import com.whitesky.youtube.util.NetworkUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Proxy;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CuteSpiderDefaultImpl implements CuteSpider{

    private static final List<String> USER_AGENTS = new ArrayList<>();

    private static final String REG_MACH_WATCH_RESP_CONTEXT = "var ytInitialPlayerResponse = (.*?);var meta";
    private static final String REG_MACH_SUGGEST_RESP_CONTEXT = "\\((.*?)\\)";

    private static final byte MATCH_TYPE_WATCH = 0;
    private static final byte MATCH_TYPE_SUGGEST = 1;

    private final Gson GSON;

    private final HashMap<String, String> HEADERS = new HashMap<>();

    private final String YT_API_WATCH = "https://www.youtube.com/watch?v=%s";
    private final String YT_API_SUGGEST = "https://suggestqueries-clients6.youtube.com/complete/search?client=youtube&hl=zh-cn&q=%s";
    private final String YT_API_SEARCH = "https://www.youtube.com/youtubei/v1/search?key=AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8&prettyPrint=false";


    private Proxy httpProxy;
    private String html = "";
    private String responseContext = "";

    private NetworkUtils.HttpRequestCallback httpResponseCallback;

    static {
        USER_AGENTS.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
    }

    public CuteSpiderDefaultImpl(){
        this(null);
    }

    public CuteSpiderDefaultImpl(Proxy proxy){
        this.httpProxy = proxy;
        GSON = new GsonBuilder()
                .registerTypeAdapter(SearchResponseContext.class, new SearchResponseContextDeserializer())
                .registerTypeAdapter(SuggestResponseContext.class, new SuggestResponseContextDeserializer())
                .registerTypeAdapter(WatchResponseContext.class, new WatchResponseContextDeserializer())
                .create();
        HEADERS.put("user-agent", USER_AGENTS.get(new Random(System.currentTimeMillis()).nextInt(USER_AGENTS.size())));
        HEADERS.put("referer", "https://www.youtube.com");
        httpResponseCallback = new NetworkUtils.HttpRequestCallback() {
            private ByteArrayOutputStream byteWriter;

            @Override
            public void onBytesResult(int state, byte[] bytes, int offset, int len) throws IOException {
                if (state != NetworkUtils.RESULT_EOF) {
                    if (Objects.isNull(byteWriter)) byteWriter = new ByteArrayOutputStream();
                    byteWriter.write(bytes, 0, len);
                } else {
                    byteWriter.close();
                    html = byteWriter.toString("UTF-8");
                    byteWriter = null;
                    countDownLatch.countDown();
                }
            }

            @Override
            public void onFailed(int statusCode, String message) {
                final String errorMsg = String.format("Failed to request. statusCode=%d, msg=%s", statusCode, message);
                System.err.println(errorMsg);
                countDownLatch.countDown();
            }
        };
    }

    @Override
    public SpiderResult<SuggestResponseContext> suggest(String kw) {
        final String suggestUrl = String.format(YT_API_SUGGEST, NetworkUtils.urlEncode(kw));
        doRequestAndReadString(suggestUrl);
        responseContext = matchResponseContext(html, MATCH_TYPE_SUGGEST);
        if (Objects.nonNull(responseContext) && !responseContext.isEmpty()) {
            final SuggestResponseContext suggestResponseContext = GSON.fromJson(responseContext, SuggestResponseContext.class);
            if (Objects.nonNull(suggestResponseContext)) return new SpiderResult(SpiderResult.RESULT_OK, suggestResponseContext);
        }
        return new SpiderResult<>(SpiderResult.RESULT_ERROR, null);
    }

    @Override
    public SpiderResult<SearchResponseContext> search(String kw) {
        final SearchParams params = new SearchParams(kw);
        doRequestAndReadString(YT_API_SEARCH, params);
        if (Objects.nonNull(html) && !html.isEmpty()) {
            responseContext = html;
            final SearchResponseContext searchResponseContext = GSON.fromJson(responseContext, SearchResponseContext.class);
            if (Objects.nonNull(searchResponseContext)) return new SpiderResult<SearchResponseContext>(SpiderResult.RESULT_OK, searchResponseContext);
        }
        return new SpiderResult<SearchResponseContext>(SpiderResult.RESULT_ERROR, null);
    }

    @Override
    public SpiderResult<WatchResponseContext> findVideoById(String videoId) {
        final String watchUrl = String.format(YT_API_WATCH, videoId);
        doRequestAndReadString(watchUrl);
        responseContext = matchResponseContext(html, MATCH_TYPE_WATCH);
        if (Objects.nonNull(responseContext) && !responseContext.isEmpty()) {
            final WatchResponseContext watchResponseContext = GSON.fromJson(responseContext, WatchResponseContext.class);
            if (Objects.nonNull(watchResponseContext)) return new SpiderResult(SpiderResult.RESULT_OK, watchResponseContext);
        }
        return new SpiderResult<>(SpiderResult.RESULT_ERROR, null);
    }

    @Override
    public String decrypting(String signatureCipher) {
        Map<String, String> signatures = Arrays.stream(signatureCipher.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> NetworkUtils.urlDecode(it[1])));
        final String url = signatures.get("url");
        final String sp = signatures.get("sp");
        final String cipher = signatures.get("s");
        char[] temp = new char[cipher.length() - 1];
        System.arraycopy(cipher.toCharArray(), 0, temp, 0, temp.length);
        temp[47 % temp.length] = temp[0];
        char[] cipherCharArray = new char[temp.length - 1];
        System.arraycopy(temp, 1, cipherCharArray, 0, cipherCharArray.length);
        return NetworkUtils.urlDecode(String.format("%s&%s=%s", url, sp, new String(cipherCharArray)));
    }

    private String matchResponseContext(String html, byte matchType) {
        Pattern pattern;
        Matcher matcher;
        switch (matchType) {
            case MATCH_TYPE_WATCH:
                pattern = Pattern.compile(REG_MACH_WATCH_RESP_CONTEXT);
                matcher = pattern.matcher(html);
                if (matcher.find()) return matcher.group(1);
                break;
            case MATCH_TYPE_SUGGEST:
                pattern = Pattern.compile(REG_MACH_SUGGEST_RESP_CONTEXT);
                matcher = pattern.matcher(html);
                if (matcher.find()) return matcher.group(1);
                break;
        }
        return null;
    }

    private class SearchParams extends NetworkUtils.PostParams {
        private final String query;
        private HashMap<String, HashMap<String, String>> context;

        public SearchParams(@NotNull String kw) {
            super(GSON);
            Objects.requireNonNull(kw);
            context = new HashMap<>();
            HashMap<String, String> client = new HashMap<>();
            client.put("hl", "en-US");
            client.put("gl", "FR");
            client.put("osName", "Windows");
            client.put("osVersion", "10.0");
            client.put("platform", "DESKTOP");
            client.put("clientName", "WEB");
            client.put("clientVersion", "2.20230427.04.00");
            client.put("browserName", "Chrome");
            client.put("timeZone", "Asia/Shanghai");
            context.put("client", client);
            this.query = kw;
        }
    }

    private void doRequestAndReadString(String url, NetworkUtils.PostParams params) {
        html = "";
        responseContext = "";
        countDownLatch = new CountDownLatch(1);
        NetworkUtils.doPost(url, httpResponseCallback, params, HEADERS, httpProxy);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private CountDownLatch countDownLatch;
    private void doRequestAndReadString(String url) {
        html = "";
        responseContext = "";
        countDownLatch = new CountDownLatch(1);
        NetworkUtils.doGet(url, httpResponseCallback, HEADERS, httpProxy);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
