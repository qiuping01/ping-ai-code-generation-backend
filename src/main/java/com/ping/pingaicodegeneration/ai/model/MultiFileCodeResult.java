package com.ping.pingaicodegeneration.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * 多文件代码生成结果模型
 */
@Description("生成多个代码文件的结果")
@Data
public class MultiFileCodeResult {

    /**
     * HTML代码内容
     */
    @Description("HTML代码")
    private String htmlCode;

    /**
     * CSS代码内容
     */
    @Description("CSS代码")
    private String cssCode;

    /**
     * JavaScript代码内容
     */
    @Description("JS代码")
    private String jsCode;

    /**
     * 代码生成结果描述
     */
    @Description("生成代码的描述")
    private String description;
}