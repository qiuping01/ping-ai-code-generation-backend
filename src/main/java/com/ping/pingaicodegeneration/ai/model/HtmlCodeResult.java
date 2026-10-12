package com.ping.pingaicodegeneration.ai.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;


/**
 * HTML代码生成结果模型
 */
@Description("生成 HTML 代码文件的结果")
@Data
public class HtmlCodeResult {

    /**
     * HTML代码内容
     */
    @Description("HTML代码")
    private String htmlCode;

    /**
     * 描述信息
     */
    @Description("生成代码的描述")
    private String description;
}