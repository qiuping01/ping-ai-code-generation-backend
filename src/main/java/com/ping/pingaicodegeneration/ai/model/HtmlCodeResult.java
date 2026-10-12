package com.ping.pingaicodegeneration.ai.model;

import lombok.Data;


/**
 * HTML代码生成结果模型
 */
@Data
public class HtmlCodeResult {

    /**
     * HTML代码内容
     */
    private String htmlCode;

    /**
     * 描述信息
     */
    private String description;
}