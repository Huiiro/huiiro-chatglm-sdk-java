package com.huiiro.chatglm.sdk.model.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tool {

    private Type type;
    private Function function;
    private Retrieval retrieval;
    @JsonProperty("web_search")
    private WebSearch webSearch;

    public String getType() {
        return type.code;
    }

    @Getter
    @AllArgsConstructor
    public static enum Type {
        function("function", "函数功能"),
        retrieval("retrieval", "知识库"),
        web_search("web_search", "联网");

        private final String code;
        private final String info;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Function {

        /**
         * 函数名称，只能包含a-z，A-Z，0-9，下划线和中横线。最大长度限制为64
         */
        private String name;

        /**
         * 用于描述函数功能。模型会根据这段描述决定函数调用方式。
         */
        private String description;

        /**
         * parameter 字段需要传入一个 Json Schema 对象，以准确地定义函数所接受的参数。
         * <a href="https://open.bigmodel.cn/dev/api#glm-3-turbo">see</a>
         */
        private Object parameters;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Retrieval {

        /**
         * 当涉及到知识库ID时，请前往开放平台的知识库模块进行创建或获取
         */
        @JsonProperty("knowledge_id")
        private String knowledgeId;

        /**
         * 请求模型时的知识库模板，默认模板
         */
        @JsonProperty("prompt_template")
        private String promptTemplate = "\"\"\"\n" +
                "{{ knowledge}}\n" +
                "\"\"\"\n" +
                "中找问题\n" +
                "\"\"\"\n" +
                "{{question}}\n" +
                "\"\"\"";
    }

    /**
     * 仅当工具类型为web_search时补充，如果tools中存在类型retrieval，此时web_search不生效。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebSearch {

        /**
         * 是否启用搜索，默认启用搜索 enable = true/false
         */
        private Boolean enable = true;

        /**
         * 强制搜索自定义关键内容，此时模型会根据自定义搜索关键内容返回的结果作为背景知识来回答用户发起的对话。
         */
        @JsonProperty("search_query")
        private String searchQuery;
    }
}
