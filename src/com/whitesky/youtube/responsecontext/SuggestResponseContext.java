package com.whitesky.youtube.responsecontext;

import java.util.ArrayList;
import java.util.List;

public class SuggestResponseContext {
    public List<String> suggests = new ArrayList<>();

    @Override
    public String toString() {
        return "SuggestResponseContext{" +
                "suggests=" + suggests +
                '}';
    }
}
