package com.ping.pingaicodegeneration.langgraph4j.node;

import com.ping.pingaicodegeneration.ai.AiCodeGenTypeRoutingService;
import com.ping.pingaicodegeneration.langgraph4j.state.WorkflowContext;
import com.ping.pingaicodegeneration.model.enums.CodeGenTypeEnum;
import com.ping.pingaicodegeneration.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * 智能路由节点
 */
@Slf4j
public class RouterNode {
    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("执行节点: 智能路由");

            CodeGenTypeEnum generationType;
            try {
                // 获取AI路由服务
                AiCodeGenTypeRoutingService routingService = SpringContextUtil.getBean(AiCodeGenTypeRoutingService.class);
                // 根据原始提示词进行智能路由
                generationType = routingService.routeCodeGenType(context.getOriginalPrompt());
            } catch (Exception e) {
                log.error("AI智能路由失败，使用默认HTML类型: {}", e.getMessage());
                generationType = CodeGenTypeEnum.HTML;
            }
            // 路由结果为空时兜底为 HTML，避免下游空指针
            if (generationType == null) {
                log.warn("AI智能路由结果为空，使用默认HTML类型");
                generationType = CodeGenTypeEnum.HTML;
            }
            log.info("AI智能路由完成，选择类型: {} ({})", generationType.getValue(), generationType.getText());

            // 更新状态
            context.setCurrentStep("智能路由");
            context.setGenerationType(generationType);
            return WorkflowContext.saveContext(context);
        });
    }
}