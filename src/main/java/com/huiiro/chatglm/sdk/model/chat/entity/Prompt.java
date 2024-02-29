package com.huiiro.chatglm.sdk.model.chat.entity;

import com.alibaba.fastjson2.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prompt {

    private String role;
    private String content;

    public static PromptBuilder builder() {
        return new PromptBuilder();
    }

    public static class PromptBuilder {
        private String role;
        private String content;

        PromptBuilder() {
        }

        public PromptBuilder role(String role) {
            this.role = role;
            return this;
        }

        public PromptBuilder content(String content) {
            this.content = content;
            return this;
        }

        public PromptBuilder content(Content content) {
            this.content = JSON.toJSONString(content);
            return this;
        }

        public Prompt build() {
            return new Prompt(this.role, this.content);
        }

        public String toString() {
            return "ChatCompletionRequest.Prompt.PromptBuilder(role=" + this.role + ", content=" + this.content + ")";
        }
    }
}
