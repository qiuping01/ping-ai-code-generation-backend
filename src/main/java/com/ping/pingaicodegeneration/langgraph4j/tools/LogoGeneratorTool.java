package com.ping.pingaicodegeneration.langgraph4j.tools;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
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
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LogoGeneratorTool {

    @Resource
    private CosManager cosManager;

    @Value("${dashscope.api-key:}")
    private String dashScopeApiKey;

    @Value("${dashscope.image-model:wan2.2-t2i-flash}")
    private String imageModel;

    @Tool("根据描述生成 Logo 设计图片，用于网站品牌标识")
    public List<ImageResource> generateLogos(@P("Logo 设计描述，如名称、行业、风格等，尽量详细") String description) {
        List<ImageResource> logoList = new ArrayList<>();
        try {
            // 构建 Logo 设计提示词
            String logoPrompt = String.format("生成 Logo，Logo 中禁止包含任何文字！Logo 介绍：%s", description);
            ImageSynthesisParam param = ImageSynthesisParam.builder()
                    .apiKey(dashScopeApiKey)
                    .model(imageModel)
                    .prompt(logoPrompt)
                    .size("512*512")
                    .n(1) // 生成 1 张足够，因为 AI 不知道哪张最好
                    .build();
            ImageSynthesis imageSynthesis = new ImageSynthesis();
            ImageSynthesisResult result = imageSynthesis.call(param);
            if (result != null && result.getOutput() != null && result.getOutput().getResults() != null) {
                List<Map<String, String>> results = result.getOutput().getResults();
                for (Map<String, String> imageResult : results) {
                    String imageUrl = imageResult.get("url");
                    if (StrUtil.isBlank(imageUrl)) {
                        continue;
                    }
                    // 转存 COS；失败回退原始 URL，不丢生成结果
                    String cosUrl = uploadImageToCos(imageUrl);
                    logoList.add(ImageResource.builder()
                            .category(ImageCategoryEnum.LOGO)
                            .description(description)
                            .url(StrUtil.isNotBlank(cosUrl) ? cosUrl : imageUrl)
                            .build());
                }
            }
        } catch (Exception e) {
            log.error("生成 Logo 失败: {}", e.getMessage(), e);
        }
        return logoList;
    }

    /**
     * 将远程图片下载并转存到 COS，失败时返回 null
     */
    private String uploadImageToCos(String imageUrl) {
        File tempFile = null;
        try {
            // 取 URL 最后一段文件名（去掉签名查询参数）的扩展名，取不到默认 png
            String fileName = StrUtil.subAfter(StrUtil.subBefore(imageUrl, "?", false), "/", true);
            String ext = FileUtil.extName(fileName);
            if (StrUtil.isBlank(ext) || ext.length() > 5) {
                ext = "png";
            }
            tempFile = FileUtil.createTempFile("logo_", "." + ext, true);
            HttpUtil.downloadFile(imageUrl, tempFile, 30_000);
            if (!tempFile.exists() || tempFile.length() == 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Logo 图片下载失败: " + imageUrl);
            }
            // 与 MermaidDiagramTool 一致：key 以 / 开头，CosManager 用 host + key 拼 URL
            String keyName = String.format("/logo/%s/%s.%s",
                    RandomUtil.randomString(5), RandomUtil.randomString(10), ext);
            return cosManager.uploadFile(keyName, tempFile);
        } catch (Exception e) {
            log.warn("Logo 转存 COS 失败，回退原始 URL: {}, 原因: {}", imageUrl, e.getMessage());
            return null;
        } finally {
            if (tempFile != null) {
                FileUtil.del(tempFile);
            }
        }
    }
}