package com.ping.pingaicodegeneration.langgraph4j.tools;

import com.ping.pingaicodegeneration.langgraph4j.model.ImageCategoryEnum;
import com.ping.pingaicodegeneration.langgraph4j.model.ImageResource;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class MermaidDiagramToolTest {

    @Resource
    private MermaidDiagramTool mermaidDiagramTool;

    @Test
    void testGenerateMermaidDiagram() {
        log.info("PATH = {}", System.getenv("PATH"));
        // 测试生成 Mermaid 架构图
        String mermaidCode = """
                flowchart LR
                    Start([开始]) --> Input[输入数据]
                    Input --> Process[处理数据]
                    Process --> Decision{是否有效?}
                    Decision -->|是| Output[输出结果]
                    Decision -->|否| Error[错误处理]
                    Output --> End([结束])
                    Error --> End
                """;
        String description = "简单系统架构图";
        List<ImageResource> diagrams = mermaidDiagramTool.generateMermaidDiagram(mermaidCode, description);
        assertNotNull(diagrams);
        // 未生成图片时给出明确失败原因，而不是 get(0) 抛 IndexOutOfBoundsException
        assertFalse(diagrams.isEmpty(), "未生成架构图，请检查 mmdc 是否可用（详见日志）");
        // 验证图表资源
        ImageResource firstDiagram = diagrams.get(0);
        assertEquals(ImageCategoryEnum.ARCHITECTURE, firstDiagram.getCategory());
        assertEquals(description, firstDiagram.getDescription());
        assertNotNull(firstDiagram.getUrl());
        assertTrue(firstDiagram.getUrl().startsWith("http"));
        System.out.println("生成了架构图: " + firstDiagram.getUrl());
    }
}
