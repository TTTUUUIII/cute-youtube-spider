package com.whitesky.youtube.responsecontext;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.whitesky.youtube.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WatchResponseContext {
    public long expiresInSeconds;
    public String videoId;
    public String title;
    public String author;
    public long lengthSeconds;
    public String shortDescription;
    public String thumbnailUrl;
    public String viewCount;
    public List<VideoFormat> videoFormats = new ArrayList<>();
    public List<AdaptiveVideoFormat> adaptiveVideoFormats = new ArrayList<>();

    public static class VideoFormat {
        public int iTag;
        public String url;
        public String mimeType;
        public int bitrate;
        public int width;
        public int height;
        public String contentLength;
        public String quality;
        public int fps;
        public String qualityLabel;
        public long approxDurationMs;
        public int audioSampleRate;
        public int audioChannels;
        public int averageBitrate;
        public String signatureCipher;

        public WatchResponseContext.VideoFormat fromJsonElement(JsonElement videoFormatElement) {
            if (videoFormatElement.isJsonObject()) {
                JsonObject format = videoFormatElement.getAsJsonObject();
                JsonElement itag = format.get("itag");
                if (Objects.nonNull(itag)) this.iTag = itag.getAsInt();
                JsonElement url = format.get("url");
                if (Objects.nonNull(url)) this.url = NetworkUtils.urlDecode(url.getAsString());
                JsonElement mimeType = format.get("mimeType");
                if (Objects.nonNull(mimeType)) this.mimeType = mimeType.getAsString();
                JsonElement bitrate = format.get("bitrate");
                if (Objects.nonNull(bitrate)) this.bitrate = bitrate.getAsInt();
                JsonElement width = format.get("width");
                if (Objects.nonNull(width)) this.width = width.getAsInt();
                JsonElement height = format.get("height");
                if (Objects.nonNull(height)) this.height = height.getAsInt();
                JsonElement contentLength = format.get("contentLength");
                if (Objects.nonNull(contentLength)) this.contentLength = contentLength.getAsString();
                JsonElement quality = format.get("quality");
                if (Objects.nonNull(quality)) this.quality = quality.getAsString();
                JsonElement fps = format.get("fps");
                if (Objects.nonNull(fps)) this.fps = fps.getAsInt();
                JsonElement qualityLabel = format.get("qualityLabel");
                if (Objects.nonNull(qualityLabel)) this.qualityLabel = qualityLabel.getAsString();
                JsonElement averageBitrate = format.get("averageBitrate");
                if (Objects.nonNull(averageBitrate)) this.averageBitrate = averageBitrate.getAsInt();
                JsonElement approxDurationMs = format.get("approxDurationMs");
                if (Objects.nonNull(approxDurationMs)) this.approxDurationMs = approxDurationMs.getAsInt();
                JsonElement audioSampleRate = format.get("audioSampleRate");
                if (Objects.nonNull(audioSampleRate)) this.audioSampleRate = audioSampleRate.getAsInt();
                JsonElement audioChannels = format.get("audioChannels");
                if (Objects.nonNull(audioChannels)) this.audioChannels = audioChannels.getAsInt();
                JsonElement signatureCipher = format.get("signatureCipher");
                if (Objects.nonNull(signatureCipher)) this.signatureCipher = signatureCipher.getAsString();
                return this;
            }
            return null;
        }

        @Override
        public String toString() {
            return "VideoFormat{" +
                    "iTag=" + iTag +
                    ", url='" + url + '\'' +
                    ", mimeType='" + mimeType + '\'' +
                    ", bitrate=" + bitrate +
                    ", width=" + width +
                    ", height=" + height +
                    ", contentLength='" + contentLength + '\'' +
                    ", quality='" + quality + '\'' +
                    ", fps=" + fps +
                    ", qualityLabel='" + qualityLabel + '\'' +
                    ", approxDurationMs=" + approxDurationMs +
                    ", audioSampleRate=" + audioSampleRate +
                    ", audioChannels=" + audioChannels +
                    ", averageBitrate=" + averageBitrate +
                    ", signatureCipher='" + signatureCipher + '\'' +
                    '}';
        }
    }

    public static class AdaptiveVideoFormat extends VideoFormat{
        public Range initRange;
        public Range indexRange;

        @Override
        public AdaptiveVideoFormat fromJsonElement(JsonElement videoFormatElement) {
            super.fromJsonElement(videoFormatElement);
            if (videoFormatElement.isJsonObject()) {
                JsonObject jsonObject = videoFormatElement.getAsJsonObject();
                JsonElement element = jsonObject.get("initRange");
                this.initRange = Range.fromJsonElement(element);
                element = jsonObject.get("indexRange");
                this.indexRange = Range.fromJsonElement(element);
            }
            return this;
        }
    }

    public static class Range {
        int start;
        int end;

        public static Range fromJsonElement(JsonElement element) {
            if (Objects.nonNull(element) && element.isJsonObject()) {
                Range range = new Range();
                JsonObject initRange = element.getAsJsonObject();
                JsonElement start = initRange.get("start");
                if (Objects.nonNull(start)) range.start = start.getAsInt();
                JsonElement end = initRange.get("end");
                if (Objects.nonNull(end)) range.end = end.getAsInt();
                return range;
            }
            return null;
        }

        @Override
        public String toString() {
            return "Range{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WatchResponseContext{" +
                "expiresInSeconds=" + expiresInSeconds +
                ", videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", lengthSeconds=" + lengthSeconds +
                ", shortDescription='" + shortDescription + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", viewCount=" + viewCount +
                ", videoFormats=" + videoFormats +
                ", adaptiveVideoFormats=" + adaptiveVideoFormats +
                '}';
    }
}
