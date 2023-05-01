package com.whitesky.youtube.deserializer;

import com.google.gson.*;
import com.whitesky.youtube.responsecontext.WatchResponseContext;

import java.lang.reflect.Type;
import java.util.Objects;

public class WatchResponseContextDeserializer implements JsonDeserializer<WatchResponseContext> {
    @Override
    public WatchResponseContext deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        WatchResponseContext watchResponseContext = null;
        if (jsonElement.isJsonObject()) {
            watchResponseContext = new WatchResponseContext();
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonElement element = jsonObject.get("videoDetails");
            if (Objects.nonNull(element) && element.isJsonObject()) {
                JsonObject videoDetails = element.getAsJsonObject();
                String videoId = videoDetails.get("videoId").getAsString();
                String title = videoDetails.get("title").getAsString();
                long lengthSeconds = videoDetails.get("lengthSeconds").getAsLong();
                String shortDescription = videoDetails.get("shortDescription").getAsString();
                String viewCount = videoDetails.get("viewCount").getAsString();
                String author = videoDetails.get("author").getAsString();
                element = videoDetails.get("thumbnail");
                if (Objects.nonNull(element) && element.isJsonObject()) {
                    JsonObject thumbnail = element.getAsJsonObject();
                    element = thumbnail.get("thumbnails");
                    if (Objects.nonNull(element) && element.isJsonArray()) {
                        JsonArray thumbnails = element.getAsJsonArray();
                        element = thumbnails.get(thumbnails.size() - 1);
                        if (Objects.nonNull(element) && element.isJsonObject()) {
                            JsonObject lastThumbnail = element.getAsJsonObject();
                            watchResponseContext.thumbnailUrl = lastThumbnail.get("url").getAsString();
                        }
                    }
                }
                watchResponseContext.videoId = videoId;
                watchResponseContext.title = title;
                watchResponseContext.lengthSeconds = lengthSeconds;
                watchResponseContext.shortDescription = shortDescription;
                watchResponseContext.viewCount = viewCount;
                watchResponseContext.author = author;
            }
            element = jsonObject.get("streamingData");
            if (Objects.nonNull(element) && element.isJsonObject()) {
                JsonObject streamingData = element.getAsJsonObject();
                long expiresInSeconds = streamingData.get("expiresInSeconds").getAsLong();
                watchResponseContext.expiresInSeconds = expiresInSeconds;
                element = streamingData.get("formats");
                if (Objects.nonNull(element) && element.isJsonArray()) {
                    JsonArray formats = element.getAsJsonArray();
                    for (JsonElement item: formats) {
                        watchResponseContext.videoFormats.add(new WatchResponseContext.VideoFormat().fromJsonElement(item));
                    }
                }
                element = streamingData.get("adaptiveFormats");
                if (Objects.nonNull(element) && element.isJsonArray()) {
                    JsonArray adaptiveFormats = element.getAsJsonArray();
                    for (JsonElement item: adaptiveFormats) {
                        watchResponseContext.adaptiveVideoFormats.add(new WatchResponseContext.AdaptiveVideoFormat().fromJsonElement(item));
                    }
                }
            }
        }
        return watchResponseContext;
    }
}
