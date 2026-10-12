package com.ping.pingaicodegeneration.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 服务创建工厂
 */
@Configuration
public class AiCodeGeneratorServiceFactory {

    @Resource
    private ChatModel chatModel;

    /**
     * 创建 AI 代码生成服务
     *
     * 原理：当在创建的时候运用反向代理机制根据传入的接口类型参数自动的生成实现类
     * 实现类的作用：调用AI，并且返回值转换成 String
     * 开发者无需关注 http 作用
     *
     * LangChain4j 在运行时通过 动态代理 帮你生成了一个实现类
     * 这个代理类负责把“方法调用”翻译成“对 ChatModel 的调用”，开发者只需要关注接口定义、注解、输入和输出。
     *
     * @return
     */
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return AiServices.create(AiCodeGeneratorService.class, chatModel);
    }
}
