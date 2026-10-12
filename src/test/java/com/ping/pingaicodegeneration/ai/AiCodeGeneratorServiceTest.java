package com.ping.pingaicodegeneration.ai;

import com.ping.pingaicodegeneration.ai.model.HtmlCodeResult;
import com.ping.pingaicodegeneration.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult htmlCode = aiCodeGeneratorService.generateHtmlCode("做个程序员阿平的博客，不超过20行代码");
        Assertions.assertNotNull(htmlCode);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult multiFileCode = aiCodeGeneratorService.generateMultiFileCode("做个程序员阿平的留言板，不超过50行代码");
        Assertions.assertNotNull(multiFileCode);
    }

}