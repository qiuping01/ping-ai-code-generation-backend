package com.ping.pingaicodegeneration.ai.model;

import lombok.Data;

/**
 * 多文件代码生成结果模型
 */
@Data
public class MultiFileCodeResult {

    /**
     * HTML代码内容
     */
    private String htmlCode;

    /**
     * CSS代码内容
     */
    private String cssCode;

    /**
     * JavaScript代码内容
     */
    private String jsCode;

    /**
     * 代码生成结果描述
     */
    private String description;
}