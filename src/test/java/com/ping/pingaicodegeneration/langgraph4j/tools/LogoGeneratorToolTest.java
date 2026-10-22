package com.ping.pingaicodegeneration.langgraph4j.tools;

import com.ping.pingaicodegeneration.langgraph4j.model.ImageCategoryEnum;
import com.ping.pingaicodegeneration.langgraph4j.model.ImageResource;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LogoGeneratorToolTest {

    @Resource
    private LogoGeneratorTool logoGeneratorTool;

    @Test
    void testGenerateLogos() {
        // 测试生成Logo
        List<ImageResource> logos = logoGeneratorTool.generateLogos("技术公司现代简约风格Logo");
        assertNotNull(logos);
        // 未生成 Logo 时给出明确失败原因，而不是 getFirst 抛 NoSuchElementException
        assertFalse(logos.isEmpty(), "未生成 Logo，请检查 DashScope 配置（详见日志）");
        ImageResource firstLogo = logos.getFirst();
        assertEquals(ImageCategoryEnum.LOGO, firstLogo.getCategory());
        assertNotNull(firstLogo.getDescription());
        assertNotNull(firstLogo.getUrl());
        logos.forEach(logo ->
                System.out.println("Logo: " + logo.getDescription() + " - " + logo.getUrl())
        );
    }
}