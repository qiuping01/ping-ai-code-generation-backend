package com.ping.pingaicodegeneration.core;

import com.ping.pingaicodegeneration.ai.AiCodeGeneratorService;
import com.ping.pingaicodegeneration.ai.model.HtmlCodeResult;
import com.ping.pingaicodegeneration.ai.model.MultiFileCodeResult;
import com.ping.pingaicodegeneration.exception.BusinessException;
import com.ping.pingaicodegeneration.exception.ErrorCode;
import com.ping.pingaicodegeneration.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成门面类，组合生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @return 保存的目录
     */
    public File generateAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> generateAndSaveHtmlCode(userMessage);
            case MULTI_FILE -> generateAndSaveMultiFileCode(userMessage);
            default -> {
                String errorMsg = "不支持的生成类型" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMsg);
            }
        };
    }

    /**
     * 生成 HTML 模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveHtmlCode(String userMessage) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
    }

    /**
     * 生成多文件模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveMultiFileCode(String userMessage) {
        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
    }


    /**
     * 根据用户消息和代码生成类型生成并保存代码流
     *
     * @param userMessage     用户输入的消息内容
     * @param codeGenTypeEnum 代码生成类型枚举
     * @return 返回一个包含生成代码的 Flux 流
     * @throws BusinessException 当生成类型为空或类型不支持时抛出业务异常
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        // 检查代码生成类型是否为空，为空则抛出参数错误异常
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        // 根据不同的代码生成类型调用对应的生成方法
        return switch (codeGenTypeEnum) {
            // 当类型为HTML时，调用HTML代码生成方法
            case HTML -> generateAndSaveHtmlCodeStream(userMessage);
            // 当类型为MULTI_FILE时，调用多文件代码生成方法
            case MULTI_FILE -> generateAndSaveMultiFileCodeStream(userMessage);
            // 默认情况，抛出系统错误异常
            default -> {
                String errorMsg = "不支持的生成类型" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMsg);
            }
        };
    }

    /**
     * 生成并保存 HTML 代码流的方法
     * 该方法通过 AI 服务生成 HTML 代码流，并在生成完成后保存代码到文件
     *
     * @param userMessage 用户输入的消息，用于生成 HTML 代码
     * @return 返回一个 Flux<String> 类型的流，用于流式返回生成的 HTML 代码
     */
    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
        Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        // 当流式返回生成代码完成后，再保存代码
        StringBuilder codeBuilder = new StringBuilder();
        return result
                .doOnNext(chunk -> {
                    // 实时收集代码
                    codeBuilder.append(chunk);
                })
                .doOnComplete(() -> {
                    // 流式返回完成后保存代码
                    try {
                        String completeHtmlCode = codeBuilder.toString();
                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
                        // 保存代码到文件
                        File savedDir = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
                        log.info("保存成功，路径为：" + savedDir.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败: {}", e.getMessage());
                    }
                });
    }


    /**
     * 生成并保存多文件代码流的私有方法
     * 该方法调用 AI 代码生成服务生成多文件代码流，并在生成完成后保存代码到文件
     *
     * @param userMessage 用户输入的消息，将用于生成代码
     * @return Flux<String> 返回一个字符串类型的响应式流，包含生成的代码块
     */
    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
        Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
        // 当流式返回生成代码完成后，再保存代码
        StringBuilder codeBuilder = new StringBuilder();
        return result
                // 对流中的每个元素进行处理
                .doOnNext(chunk -> {
                    // 实时收集代码块到StringBuilder中
                    codeBuilder.append(chunk);
                })
                // 当流完成时执行的操作
                .doOnComplete(() -> {
                    // 流式返回完成后保存代码
                    try {
                        // 将所有代码块合并为完整代码
                        String completeMultiCode = codeBuilder.toString();
                        // 解析多文件代码结构
                        MultiFileCodeResult multiFileCodeResult = CodeParser.parseMultiFileCode(completeMultiCode);
                        // 保存代码到文件
                        File savedMulti = CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
                        log.info("保存成功，路径为：" + savedMulti.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败: {}", e.getMessage());
                    }
                });
    }
}
