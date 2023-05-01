package com.whitesky.youtube.deserializer;

import com.google.gson.*;
import com.whitesky.youtube.responsecontext.SuggestResponseContext;

import java.lang.reflect.Type;

public class SuggestResponseContextDeserializer implements JsonDeserializer<SuggestResponseContext> {
    @Override
    public SuggestResponseContext deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        SuggestResponseContext suggestResponseContext = null;
        if (jsonElement.isJsonArray()) {
            suggestResponseContext = new SuggestResponseContext();
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (int i = 0; i < jsonArray.size(); ++i) {
                JsonElement element = jsonArray.get(i);
                if (element.isJsonArray()) {
                    jsonArray = element.getAsJsonArray();
                    for (int j = 0; j < jsonArray.size() - 1; ++j) {
                        JsonElement item = jsonArray.get(j);
                        if (item.isJsonArray()) {
                            JsonArray itemArray = item.getAsJsonArray();
                            JsonElement temp = itemArray.get(0);
                            if (temp.isJsonPrimitive()) {
                                suggestResponseContext.suggests.add(temp.getAsString());
                            }
                        }
                    }
                }
            }
        }
        return suggestResponseContext;
    }
}
