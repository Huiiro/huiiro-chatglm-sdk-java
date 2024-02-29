package com.huiiro.chatglm.sdk.model.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.huiiro.chatglm.sdk.model.chat.entity.Prompt;
import com.huiiro.chatglm.sdk.model.chat.entity.Tool;
import com.huiiro.chatglm.sdk.model.common.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatCompletionRequest {

    /**
     * 是否对返回结果数据做兼容，24年1月发布的 GLM_3_5_TURBO、GLM_4 模型，与之前的模型在返回结果上有差异。开启 true 可以做兼容。
     */
    private Boolean isCompatible = true;

    /**
     * 模型
     */
    private Model model = Model.GLM_3_5_TURBO;

    /**
     * 请求参数 {"role": "user", "content": "你好"}
     * 24年1月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    private List<Prompt> messages;

    /**
     * 请求ID
     */
    @JsonProperty("request_id")
    private String requestId = String.format("xfg-%d", System.currentTimeMillis());

    /**
     * do_sample 为 true 时启用采样策略，do_sample 为 false 时采样策略 temperature、top_p 将不生效
     * 24年1月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    @JsonProperty("do_sample")
    private Boolean doSample = true;

    /**
     * 使用同步调用时，此参数应当设置为 Fasle 或者省略。表示模型生成完所有内容后一次性返回所有内容。
     * 如果设置为 True，模型将通过标准 Event Stream ，逐块返回模型生成内容。Event Stream 结束时会返回一条data: [DONE]消息。
     * 24年1月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    private Boolean stream = true;

    /**
     * 控制温度【随机性】
     */
    private float temperature = 0.9f;

    /**
     * 多样性控制；
     */
    @JsonProperty("top_p")
    private float topP = 0.7f;

    /**
     * 模型输出最大tokens
     * 24年1月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    @JsonProperty("max_tokens")
    private Integer maxTokens = 2048;

    /**
     * 模型在遇到stop所制定的字符时将停止生成，目前仅支持单个停止词，格式为["stop_word1"]
     * 24年1月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    private List<String> stop;

    /**
     * 可供模型调用的工具列表,tools字段会计算 tokens ，同样受到tokens长度的限制
     * 24年1月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    private List<Tool> tools;

    /**
     * 用于控制模型是如何选择要调用的函数，仅当工具类型为function时补充。默认为auto，当前仅支持auto。
     * 24年1月发布的 GLM_3_5_TURBO、GLM_4 模型时新增
     */
    @JsonProperty("tool_choice")
    private String toolChoice = "auto";

    /**
     * 输入给模型的会话信息
     * 用户输入的内容；role=user
     * 挟带历史的内容；role=assistant
     */
    private List<Prompt> prompt;

    /**
     * 智普AI sse 固定参数 incremental = true 【增量返回】
     */
    private boolean incremental = true;

    /**
     * sseformat, 用于兼容解决sse增量模式okhttpsse截取data:后面空格问题, [data: hello]。只在增量模式下使用sseFormat。
     */
    private String sseFormat = "data";
}
