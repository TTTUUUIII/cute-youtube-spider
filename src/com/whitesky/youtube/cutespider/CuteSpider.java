package com.whitesky.youtube.cutespider;

import com.whitesky.youtube.responsecontext.SearchResponseContext;
import com.whitesky.youtube.responsecontext.SuggestResponseContext;
import com.whitesky.youtube.responsecontext.WatchResponseContext;

public interface CuteSpider {

    /**
     * get some search suggestions from YouTube.
     *
     * @param kw
     * @return
     */
    SpiderResult<SuggestResponseContext> suggest(String kw);

    /**
     * search from YouTube.
     *
     * @param kw
     * @return
     */
    SpiderResult<SearchResponseContext> search(String kw);

    /**
     * get video info by video's id.
     *
     * @param videoId
     * @return
     */
    SpiderResult<WatchResponseContext> findVideoById(String videoId);

    /**
     * to decrypt the encrypted video url.
     *
     * @param signatureCipher
     * @return the video's real url.
     */
    String decrypting(String signatureCipher);
}
