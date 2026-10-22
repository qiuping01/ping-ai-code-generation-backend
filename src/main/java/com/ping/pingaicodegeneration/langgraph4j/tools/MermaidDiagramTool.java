package com.ping.pingaicodegeneration.langgraph4j.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.ping.pingaicodegeneration.exception.BusinessException;
import com.ping.pingaicodegeneration.exception.ErrorCode;
import com.ping.pingaicodegeneration.langgraph4j.model.ImageCategoryEnum;
import com.ping.pingaicodegeneration.langgraph4j.model.ImageResource;
import com.ping.pingaicodegeneration.manager.CosManager;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Meramid 架构图生成工具（根据关键词搜索内容图片）
 */
@Slf4j
@Component
public class MermaidDiagramTool {

    @Resource
    private CosManager cosManager;

    /**
     * Puppeteer 使用的浏览器路径。配置后通过 -p 传给 mmdc；
     * 留空则由 mmdc 使用 Puppeteer 自己下载的 Chrome
     */
    @Value("${mermaid.puppeteer.executable-path:}")
    private String puppeteerExecutablePath;

    @Tool("将 Mermaid 代码转换为架构图图片，用于展示系统结构和技术关系")
    public List<ImageResource> generateMermaidDiagram(@P("Mermaid 图表代码") String mermaidCode,
                                                      @P("架构图描述") String description) {
        if (StrUtil.isBlank(mermaidCode)) {
            return new ArrayList<>();
        }
        File diagramFile = null;
        try {
            // 转换为SVG图片
            diagramFile = convertMermaidToSvg(mermaidCode);
            // 上传到COS
            String keyName = String.format("/mermaid/%s/%s",
                    RandomUtil.randomString(5), diagramFile.getName());
            String cosUrl = cosManager.uploadFile(keyName, diagramFile);
            if (StrUtil.isNotBlank(cosUrl)) {
                return Collections.singletonList(ImageResource.builder()
                        .category(ImageCategoryEnum.ARCHITECTURE)
                        .description(description)
                        .url(cosUrl)
                        .build());
            }
        } catch (Exception e) {
            log.error("生成架构图失败: {}", e.getMessage(), e);
        } finally {
            // 无论上传成功与否都清理临时文件
            if (diagramFile != null) {
                FileUtil.del(diagramFile);
            }
        }
        return new ArrayList<>();
    }

    /**
     * 将Mermaid代码转换为SVG图片
     */
    private File convertMermaidToSvg(String mermaidCode) {
        // 创建临时输入文件
        File tempInputFile = FileUtil.createTempFile("mermaid_input_", ".mmd", true);
        FileUtil.writeUtf8String(mermaidCode, tempInputFile);
        // 创建临时输出文件
        File tempOutputFile = FileUtil.createTempFile("mermaid_output_", ".svg", true);
        // 指定浏览器时生成 Puppeteer 配置
        File puppeteerConfigFile = createPuppeteerConfigFile();
        try {
            // 根据操作系统选择命令
            String command = SystemUtil.getOsInfo().isWindows() ? "mmdc.cmd" : "mmdc";
            // 构建命令，逐个参数传入，避免路径含空格时被拆开
            List<String> commands = new ArrayList<>();
            commands.add(command);
            commands.add("-i");
            commands.add(tempInputFile.getAbsolutePath());
            commands.add("-o");
            commands.add(tempOutputFile.getAbsolutePath());
            commands.add("-b");
            commands.add("transparent");
            if (puppeteerConfigFile != null) {
                commands.add("-p");
                commands.add(puppeteerConfigFile.getAbsolutePath());
            }
            // 执行命令
            String executeResult = RuntimeUtil.execForStr(commands.toArray(new String[0]));
            // 检查输出文件
            if (!tempOutputFile.exists() || tempOutputFile.length() == 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Mermaid CLI 执行失败: " + executeResult);
            }
            return tempOutputFile;
        } catch (RuntimeException e) {
            // 失败时清理输出文件，避免留下空文件
            FileUtil.del(tempOutputFile);
            throw e;
        } finally {
            // 清理输入文件和配置文件，保留输出文件供上传使用
            FileUtil.del(tempInputFile);
            if (puppeteerConfigFile != null) {
                FileUtil.del(puppeteerConfigFile);
            }
        }
    }

    /**
     * 生成 Puppeteer 配置文件（用于指定 mmdc 使用的浏览器），未配置浏览器路径时返回 null
     */
    private File createPuppeteerConfigFile() {
        if (StrUtil.isBlank(puppeteerExecutablePath)) {
            return null;
        }
        File configFile = FileUtil.createTempFile("puppeteer_config_", ".json", true);
        String config = JSONUtil.createObj()
                .set("executablePath", puppeteerExecutablePath)
                .toString();
        FileUtil.writeUtf8String(config, configFile);
        return configFile;
    }
}