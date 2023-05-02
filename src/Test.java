import com.whitesky.youtube.cutespider.CuteSpider;
import com.whitesky.youtube.cutespider.CuteSpiderDefaultImpl;
import com.whitesky.youtube.cutespider.SpiderResult;
import com.whitesky.youtube.responsecontext.SearchResponseContext;
import com.whitesky.youtube.responsecontext.SuggestResponseContext;
import com.whitesky.youtube.responsecontext.WatchResponseContext;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;

public class Test {
    public static void main(String[] args) {
        final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809));
        final CuteSpiderDefaultImpl cuteSpider = new CuteSpiderDefaultImpl(proxy);
//        testApiSuggest("big bu", cuteSpider);
//        testApiFindVideoById("aqz-KE-bpKQ", cuteSpider);
        testApiSearch("big buck bunny", cuteSpider);
    }

    private static void testApiSuggest(String kw, CuteSpider cuteSpider) {
        SpiderResult<SuggestResponseContext> suggest = cuteSpider.suggest(kw);
        if (suggest.code == SpiderResult.RESULT_OK) {
            System.out.println(suggest.body);
        } else {
            System.err.println("Failed to request the server.");
        }
    }

    private static void testApiSearch(String kw, CuteSpider cuteSpider) {
        final SpiderResult<SearchResponseContext> search = cuteSpider.search(kw);
        if (search.code == SpiderResult.RESULT_OK) {
            System.out.println(search.body);
        }else {
            System.err.println("No search result.");
        }
    }

    private static void testApiFindVideoById(String videoId, CuteSpider cuteSpider) {
        SpiderResult<WatchResponseContext> watchContext = cuteSpider.findVideoById(videoId);
        if (watchContext.code == SpiderResult.RESULT_OK) {
            WatchResponseContext.VideoFormat videoFormat = watchContext.body.videoFormats.get(0);
            String videoUrl;
            if (Objects.nonNull(videoFormat.signatureCipher)) {
                videoUrl = cuteSpider.decrypting(videoFormat.signatureCipher);
            } else {
                videoUrl = videoFormat.url;
            }
            System.out.println(videoUrl);
        } else {
            System.err.println("Failed to request the server.");
        }
    }
}
