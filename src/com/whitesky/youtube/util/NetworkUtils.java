package com.whitesky.youtube.util;
import com.google.gson.Gson;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

public final class NetworkUtils {

    public static int RESULT_READING = 0;
    public static int RESULT_EOF = -1;


    private NetworkUtils() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("The class should not be instantiated!"); /*disable instantiation.*/
    }

    /**
     * send a get request.
     *
     * @param httpUrl
     * @param resultCallback
     * @param headers
     * @param proxy
     */
    public static void doGet(String httpUrl,HttpRequestCallback resultCallback,  @Nullable HashMap<String, String> headers, @Nullable Proxy proxy){
        try {
            final HttpURLConnection connection = openHttpConnection(httpUrl, headers, proxy);
            connection.setRequestMethod("GET");
            handleResponse(connection, resultCallback);
        } catch (IOException ioException) {
            resultCallback.onFailed(-1, ioException.getMessage());
        }
    }

    /**
     * send a post request.
     *
     * @param httpUrl
     * @param resultCallback
     * @param params
     * @param headers
     * @param proxy
     */
    public static void doPost(String httpUrl,HttpRequestCallback resultCallback,  @Nullable PostParams params, @Nullable HashMap<String, String> headers, @Nullable Proxy proxy){
        try {
            final HttpURLConnection connection = openHttpConnection(httpUrl, headers, proxy);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            if (Objects.nonNull(params)) {
                connection.setDoOutput(true);
                try (final OutputStream outputStream = connection.getOutputStream();){
                    outputStream.write(params.toBytes());
                    outputStream.flush();
                }
            }
            handleResponse(connection, resultCallback);
        } catch (IOException ioException) {
            resultCallback.onFailed(-1, ioException.getMessage());
        }
    }

    /**
     * decode url.
     *
     * @param url
     * @return
     */
    public static String urlDecode(String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Can't to decode the url. `" + url + "`");
        }
        return url;
    }

    /**
     * encode url.
     *
     * @param url
     * @return
     */
    public static String urlEncode(String url) {
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Can't to decode the url. `" + url + "`");
        }
        return url;
    }

    public interface HttpRequestCallback {

        /**
         * write the response data back to the requester.
         *
         * @param state if the state is RESULT_EOF, indicates that response data is written to complete.
         * @param bytes a block of response dara.
         * @param offset
         * @param len length of the block.
         * @throws IOException
         */
        void onBytesResult(int state, @Nullable byte[] bytes, int offset, int len) throws IOException;

        /**
         * if failed to send request, this method will be call.
         *
         * @param statusCode http status code.
         * @param message  error message.
         */
        void onFailed(int statusCode, String message);
    }

    public static abstract class PostParams {

        public transient WeakReference<Gson> wGson;

        public PostParams(@Nullable Gson gson) {
            this.wGson = new WeakReference<>(gson);
        }

        public byte[] toBytes(Charset charset) {
            return this.toString().getBytes(charset);
        }

        public byte[] toBytes() {
            return toBytes(StandardCharsets.UTF_8);
        }

        @Override
        public String toString() {
            Gson gson = this.wGson.get();
            if (Objects.isNull(gson)) gson = new Gson();
            return gson.toJson(this);
        }
    }

    private static HttpURLConnection openHttpConnection(String httpUrl, @Nullable HashMap<String, String> headers, @Nullable Proxy proxy) throws IOException {
        final URL url = new URL(httpUrl);
        final HttpURLConnection connection;
        if (Objects.nonNull(proxy)) {
            connection = (HttpURLConnection) url.openConnection(proxy);
        } else {
            connection = (HttpURLConnection) url.openConnection();
        }
        if (Objects.nonNull(headers)) {
            headers.forEach((k, v) -> connection.setRequestProperty(k, v));
        }
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        return connection;
    }

    private static void handleResponse(HttpURLConnection connection, HttpRequestCallback resultCallback) throws IOException {
        final int statusCode = connection.getResponseCode();
        final byte[] buffer = new byte[512];
        int readLen;
        if (statusCode == 200) {
            try (InputStream inputStream = connection.getInputStream()){
                while ((readLen = inputStream.read(buffer, 0, buffer.length)) != -1)
                {
                    resultCallback.onBytesResult(RESULT_READING, buffer, 0, readLen);
                }
                resultCallback.onBytesResult(RESULT_EOF, null, 0, 0);
            }
        } else {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))){
                String line;
                while ((line = reader.readLine()) != null) {
                    System.err.println(line);
                }
            }
            resultCallback.onFailed(statusCode, connection.getResponseMessage());
        }
    }
}
