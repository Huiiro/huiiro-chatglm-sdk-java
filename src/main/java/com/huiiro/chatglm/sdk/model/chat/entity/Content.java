package com.huiiro.chatglm.sdk.model.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    private String type = Type.text.code;
    private String text;
    @JsonProperty("image_url")
    private ImageUrl imageUrl;

    @Getter
    @AllArgsConstructor
    public enum Type {
        text("text", "文本"),
        image_url("image_url", "图");

        private final String code;
        private final String info;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageUrl {
        private String url;
    }

}