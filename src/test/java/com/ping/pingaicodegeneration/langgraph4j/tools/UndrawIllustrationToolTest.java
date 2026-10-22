package com.ping.pingaicodegeneration.langgraph4j.tools;

import com.ping.pingaicodegeneration.langgraph4j.model.ImageCategoryEnum;
import com.ping.pingaicodegeneration.langgraph4j.model.ImageResource;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UndrawIllustrationToolTest {

    @Resource
    private UndrawIllustrationTool undrawIllustrationTool;

    @Test
    void testSearchIllustrations() {
        // 测试正常搜索插画
        List<ImageResource> illustrations = undrawIllustrationTool.searchIllustrations("happy");
        assertNotNull(illustrations);
        // 未搜索到插画时给出明确失败原因，而不是 get(0) 抛 IndexOutOfBoundsException
        assertFalse(illustrations.isEmpty(), "未搜索到插画，请检查 undraw 接口是否可用（详见日志）");
        // 验证返回的插画资源
        ImageResource firstIllustration = illustrations.get(0);
        assertEquals(ImageCategoryEnum.ILLUSTRATION, firstIllustration.getCategory());
        assertNotNull(firstIllustration.getDescription());
        assertNotNull(firstIllustration.getUrl());
        assertTrue(firstIllustration.getUrl().startsWith("http"));
        System.out.println("搜索到 " + illustrations.size() + " 张插画");
        illustrations.forEach(illustration ->
                System.out.println("插画: " + illustration.getDescription() + " - " + illustration.getUrl())
        );
    }
}