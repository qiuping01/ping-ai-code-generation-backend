package com.ping.pingaicodegeneration.service;

import com.mybatisflex.core.service.IService;
import com.ping.pingaicodegeneration.model.entity.ChatHistory;

/**
 * 对话历史 服务层。
 *
 * @author ping
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 添加对话历史
     *
     * @param appId       应用ID，用于标识不同的应用
     * @param message     聊天消息内容
     * @param messageType 消息类型，用于区分不同种类的消息
     * @param userId      用户ID，标识发送消息的用户
     * @return 返回一个布尔值，表示添加消息是否成功
     */
    boolean addChatMessage(Long appId, String message, String messageType, Long userId);

    /**
     * 根据应用 ID 删除对话历史
     *
     * @param appId 应用 ID
     * @return 返回一个布尔值，表示删除是否成功
     */
    boolean deleteByAppId(Long appId);
}
