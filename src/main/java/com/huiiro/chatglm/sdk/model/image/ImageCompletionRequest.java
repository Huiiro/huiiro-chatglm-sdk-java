package com.huiiro.chatglm.sdk.model.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huiiro.chatglm.sdk.model.common.Model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * CogView Model
 * 根据用户的文字描述生成图像,使用同步调用方式请求接口
 * <a href="https://open.bigmodel.cn/dev/api#cogview">doc</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageCompletionRequest {

    private Model model = Model.COGVIEW_3;

    /**
     * 所需图像的文本描述
     */
    private String prompt;

    public String getModel() {
        return model.getCode();
    }

    public Model getModelEnum() {
        return model;
    }

    @Override
    public String toString() {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("model", model.getCode());
        paramsMap.put("prompt", prompt);
        try {
            return new ObjectMapper().writeValueAsString(paramsMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}

