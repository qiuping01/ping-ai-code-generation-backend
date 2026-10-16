package com.ping.pingaicodegeneration.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ping.pingaicodegeneration.model.entity.ChatHistory;
import com.ping.pingaicodegeneration.mapper.ChatHistoryMapper;
import com.ping.pingaicodegeneration.service.ChatHistoryService;
import org.springframework.stereotype.Service;

/**
 * 对话历史 服务层实现。
 *
 * @author ping
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{

}
