package com.whitesky.youtube.cutespider;

import com.sun.istack.internal.Nullable;

public class SpiderResult <T>{

    public static final byte RESULT_OK = 0;
    public static final byte RESULT_ERROR = -1;

    public byte code;
    public final T body;

    public SpiderResult(byte resultCode, @Nullable T data) {
        this.code = resultCode;
        this.body = data;
    }
}
