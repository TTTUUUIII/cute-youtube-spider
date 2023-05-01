package com.whitesky.youtube.responsecontext;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchResponseContext {
    public final String estimatedResults;
    public final List<VideoRenderer> contents = new ArrayList<>();

    public SearchResponseContext(String estimatedResults) {
        this.estimatedResults = estimatedResults;
    }

    public static class VideoRenderer {
        public String videoId;
        public String thumbnailUrl;
        public String title;
        public String label;
        public String publishedTimeText;
        public String lengthText;
        public String viewCountText;
        public String description;
        public Owner owner;

        public VideoRenderer fromJsonElement(JsonElement jsonElement) {
            if (jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement element = jsonObject.get("videoRenderer");
                if (Objects.nonNull(element) && element.isJsonObject()) {
                    jsonObject = element.getAsJsonObject();
                    element = jsonObject.get("videoId");
                    this.videoId = element.getAsString();
                    element = jsonObject.get("thumbnail");
                    if (element.isJsonObject()) {
                        element = element.getAsJsonObject()
                                .get("thumbnails");
                        if (Objects.nonNull(element) && element.isJsonArray()) {
                            JsonArray jsonArray = element.getAsJsonArray();
                            if (!jsonArray.isEmpty()) {
                                element = jsonArray.get(jsonArray.size() - 1);
                                if (element.isJsonObject()) {
                                    JsonObject thumbnailObject = element.getAsJsonObject();
                                    element = thumbnailObject.get("url");
                                    if (Objects.nonNull(element)) this.thumbnailUrl = element.getAsString();
                                }
                            }
                        }
                    }
                    element = jsonObject.get("title");
                    if (element.isJsonObject()) {
                        JsonObject titleObject = element.getAsJsonObject();
                        JsonElement runs = titleObject.get("runs");
                        if (runs.isJsonArray()) {
                            JsonArray runsArray = runs.getAsJsonArray();
                            element = runsArray.get(0);
                            if (element.isJsonObject()) {
                                JsonElement title = element.getAsJsonObject()
                                        .get("text");
                                this.title = title.getAsString();
                            }
                        }
                        element = titleObject.get("accessibility");
                        if (element.isJsonObject()) {
                            element = element.getAsJsonObject()
                                    .get("accessibilityData");
                            if (element.isJsonObject()) {
                                JsonElement label = element.getAsJsonObject()
                                        .get("label");
                                this.label = label.getAsString();
                            }
                        }
                    }
                    element = jsonObject.get("publishedTimeText");
                    if (Objects.nonNull(element) && element.isJsonObject()) {
                        JsonElement publishedTimeText = element.getAsJsonObject()
                                .get("simpleText");
                        this.publishedTimeText = publishedTimeText.getAsString();
                    }
                    element = jsonObject.get("lengthText");
                    if (element.isJsonObject()) {
                        JsonElement lengthText = element.getAsJsonObject()
                                .get("simpleText");
                        this.lengthText = lengthText.getAsString();
                    }
                    element = jsonObject.get("viewCountText");
                    if (element.isJsonObject()) {
                        JsonElement viewCountText = element.getAsJsonObject()
                                .get("simpleText");
                        this.viewCountText = viewCountText.getAsString();
                    }
                    element = jsonObject.get("ownerText");
                    if (element.isJsonObject()) {
                        JsonObject ownerObject = element.getAsJsonObject();
                        element = ownerObject.get("runs");
                        if (element.isJsonArray()) {
                            JsonArray runsArray = element.getAsJsonArray();
                            if (!runsArray.isEmpty()) {
                                element = runsArray.get(0);
                                if (element.isJsonObject()) {
                                    String ownerThumbnailsUrl = "";
                                    String ownerNameText = "";
                                    element = element.getAsJsonObject()
                                            .get("text");
                                    if (Objects.nonNull(element)) ownerNameText = element.getAsString();
                                    element = jsonObject.get("channelThumbnailSupportedRenderers");
                                    if (element.isJsonObject()) {
                                        element = element.getAsJsonObject()
                                                .get("channelThumbnailWithLinkRenderer");
                                        if (element.isJsonObject()) {
                                            element = element.getAsJsonObject()
                                                    .get("thumbnail");
                                            if (element.isJsonObject()) {
                                                element = element.getAsJsonObject()
                                                        .get("thumbnails");
                                                if (element.isJsonArray()) {
                                                    JsonArray thumbnailArray = element.getAsJsonArray();
                                                    if (!thumbnailArray.isEmpty()) {
                                                        element = thumbnailArray.get(0);
                                                        if (element.isJsonObject()) {
                                                            element = element.getAsJsonObject()
                                                                    .get("url");
                                                            if (Objects.nonNull(element)) {
                                                                ownerThumbnailsUrl = element.getAsString();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    this.owner = new Owner(ownerNameText, ownerThumbnailsUrl);
                                }
                            }
                        }
                    }
                    element = jsonObject.get("detailedMetadataSnippets");
                    if (Objects.nonNull(element) && element.isJsonArray()) {
                        JsonArray jsonArray = element.getAsJsonArray();
                        if (!jsonArray.isEmpty()) {
                            element = jsonArray.get(0);
                            if (element.isJsonObject()) {
                                element = element.getAsJsonObject()
                                        .get("snippetText");
                                if (element.isJsonObject()) {
                                    element = element.getAsJsonObject()
                                            .get("runs");
                                    if (Objects.nonNull(element) && element.isJsonArray()) {
                                        jsonArray = element.getAsJsonArray();
                                        if (!jsonArray.isEmpty()) {
                                            element = jsonArray.get(jsonArray.size() - 1);
                                            if (element.isJsonObject()) {
                                                element = element.getAsJsonObject()
                                                        .get("text");
                                                if (Objects.nonNull(element)) this.description = element.getAsString();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    return this;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "VideoRenderer{" +
                    "videoId='" + videoId + '\'' +
                    ", thumbnailsUrl='" + thumbnailUrl + '\'' +
                    ", title='" + title + '\'' +
                    ", label='" + label + '\'' +
                    ", publishedTimeText='" + publishedTimeText + '\'' +
                    ", lengthText='" + lengthText + '\'' +
                    ", viewCountText='" + viewCountText + '\'' +
                    ", description='" + description + '\'' +
                    ", owner=" + owner +
                    '}';
        }
    }

    public static class Owner {
        public final String name;
        public final String thumbnailUrl;
        public Owner(String name, String thumbnailsUrl) {
            this.name = name;
            this.thumbnailUrl = thumbnailsUrl;
        }

        @Override
        public String toString() {
            return "Owner{" +
                    "name='" + name + '\'' +
                    ", thumbnailsUrl='" + thumbnailUrl + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "SearchResponseContext{" +
                "estimatedResults='" + estimatedResults + '\'' +
                ", contents=" + contents +
                '}';
    }
}
