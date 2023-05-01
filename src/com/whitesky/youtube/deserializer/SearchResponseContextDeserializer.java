package com.whitesky.youtube.deserializer;

import com.google.gson.*;
import com.whitesky.youtube.responsecontext.SearchResponseContext;

import java.lang.reflect.Type;
import java.util.Objects;

public class SearchResponseContextDeserializer implements JsonDeserializer<SearchResponseContext> {
    @Override
    public SearchResponseContext deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        SearchResponseContext searchResponseContext = null;
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            String estimatedResults = "";
            JsonElement element = jsonObject.get("estimatedResults");
            if (element.isJsonPrimitive()) {
                estimatedResults = element.getAsString();
            }
            searchResponseContext = new SearchResponseContext(estimatedResults);
            element = jsonObject.get("contents");
            if (element.isJsonObject()) {
                jsonObject = element.getAsJsonObject();
                element = jsonObject.get("twoColumnSearchResultsRenderer");
                if (element.isJsonObject()) {
                    jsonObject = element.getAsJsonObject();
                    element = jsonObject.get("primaryContents");
                    if (element.isJsonObject()) {
                        jsonObject = element.getAsJsonObject();
                        element = jsonObject.get("sectionListRenderer");
                        if (element.isJsonObject()) {
                            element = element.getAsJsonObject()
                                    .get("contents");
                            if (element.isJsonArray()) {
                                JsonArray contents = element.getAsJsonArray();
                                if (!contents.isEmpty()) {
                                    element = contents.get(0);
                                    if (element.isJsonObject()) {
                                        jsonObject = element.getAsJsonObject();
                                        element = jsonObject.get("itemSectionRenderer");
                                        if (element.isJsonObject()) {
                                            jsonObject = element.getAsJsonObject();
                                            element = jsonObject.get("contents");
                                            if (element.isJsonArray()) {
                                                contents = element.getAsJsonArray();
                                                for (JsonElement videoRendererElement: contents) {
                                                    SearchResponseContext.VideoRenderer videoRenderer = new SearchResponseContext.VideoRenderer().fromJsonElement(videoRendererElement);
                                                    if (Objects.nonNull(videoRenderer)) {
                                                        searchResponseContext.contents.add(videoRenderer);
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        return searchResponseContext;
    }
}
