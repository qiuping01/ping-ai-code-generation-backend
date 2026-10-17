package com.ping.pingaicodegeneration.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ping.pingaicodegeneration.constant.UserConstant;
import com.ping.pingaicodegeneration.exception.ErrorCode;
import com.ping.pingaicodegeneration.exception.ThrowUtils;
import com.ping.pingaicodegeneration.model.dto.chathistory.ChatHistoryQueryRequest;
import com.ping.pingaicodegeneration.model.entity.App;
import com.ping.pingaicodegeneration.model.entity.ChatHistory;
import com.ping.pingaicodegeneration.mapper.ChatHistoryMapper;
import com.ping.pingaicodegeneration.model.entity.User;
import com.ping.pingaicodegeneration.model.enums.ChatHistoryMessageTypeEnum;
import com.ping.pingaicodegeneration.service.AppService;
import com.ping.pingaicodegeneration.service.ChatHistoryService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层实现。
 *
 * @author ping
 */
@Slf4j
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    @Resource
    @Lazy
    private AppService appService;

    /**
     * 添加对话历史
     *
     * @param appId       应用ID，必须大于0
     * @param message     消息内容，不能为空
     * @param messageType 消息类型，不能为空
     * @param userId      用户ID，必须大于0
     * @return 添加成功返回true，否则返回false
     */
    @Override
    public boolean addChatMessage(Long appId, String message, String messageType, Long userId) {
        // 验证应用ID是否有效
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        // 验证消息内容是否为空
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "消息内容不能为空");
        // 验证消息类型是否为空
        ThrowUtils.throwIf(StrUtil.isBlank(messageType), ErrorCode.PARAMS_ERROR, "消息类型不能为空");
        // 验证用户ID是否有效
        ThrowUtils.throwIf(userId == null || userId <= 0, ErrorCode.PARAMS_ERROR, "用户ID不能为空");
        // 验证消息类型是否有效
        ChatHistoryMessageTypeEnum messageTypeEnum = ChatHistoryMessageTypeEnum.getEnumByValue(messageType);
        ThrowUtils.throwIf(messageTypeEnum == null, ErrorCode.PARAMS_ERROR, "不支持的消息类型: " + messageType);
        // 构建聊天历史记录对象
        ChatHistory chatHistory = ChatHistory.builder()
                .appId(appId)
                .message(message)
                .messageType(messageType)
                .userId(userId)
                .build();
        // 保存聊天记录并返回结果
        return this.save(chatHistory);
    }

    /**
     * 根据应用 ID 删除对话历史
     *
     * @param appId 应用 ID
     * @return 返回一个布尔值，表示删除是否成功
     */
    @Override
    public boolean deleteByAppId(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        QueryWrapper queryWrapper = QueryWrapper.create()
                .eq("appId", appId);
        return this.remove(queryWrapper);
    }

    /**
     * 根据应用 ID 分页查询对话历史
     *
     * @param appId          应用 ID
     * @param pageSize       每页大小
     * @param lastCreateTime 最后创建时间
     * @param loginUser      当前登录用户
     * @return 返回一个 Page 对象，包含分页后的对话历史记录
     */
    @Override
    public Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize,
                                                      LocalDateTime lastCreateTime,
                                                      User loginUser) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID不能为空");
        ThrowUtils.throwIf(pageSize <= 0 || pageSize > 50, ErrorCode.PARAMS_ERROR, "页面大小必须在1-50之间");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 验证权限：只有应用创建者和管理员可以查看
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        boolean isAdmin = UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole());
        boolean isCreator = app.getUserId().equals(loginUser.getId());
        ThrowUtils.throwIf(!isAdmin && !isCreator, ErrorCode.NO_AUTH_ERROR, "无权查看该应用的对话历史");
        // 构建查询条件
        ChatHistoryQueryRequest queryRequest = new ChatHistoryQueryRequest();
        queryRequest.setAppId(appId);
        queryRequest.setLastCreateTime(lastCreateTime);
        QueryWrapper queryWrapper = this.getQueryWrapper(queryRequest);
        // 查询数据
        return this.page(Page.of(1, pageSize), queryWrapper);
    }

    /**
     * 加载对话历史到内存
     * <p>
     * 惰性恢复：只有记忆中确实没有内容时才从数据库回填。记忆本身由 ChatMemoryStore 持久化，
     * 只要没到期，Redis 里那份总比数据库更新鲜，用数据库覆盖会把缓存中的记忆整体回滚
     *
     * @param appId      应用 ID
     * @param chatMemory 对话记忆
     * @param maxCount   最大加载条数
     * @return 加载的条数，无需加载时返回 0
     */
    @Override
    public int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount) {
        try {
            // 记忆中已有历史（Redis 中的数据尚未过期），无需从数据库回填
            if (CollUtil.isNotEmpty(chatMemory.messages())) {
                log.debug("appId: {} 的对话记忆已存在，跳过从数据库加载", appId);
                return 0;
            }
            // 直接构造查询条件，起始点为 1 而不是 0，用于排除最新的用户消息
            QueryWrapper queryWrapper = QueryWrapper.create()
                    .eq(ChatHistory::getAppId, appId)
                    .orderBy(ChatHistory::getCreateTime, false)
                    .limit(1, maxCount);
            List<ChatHistory> historyList = this.list(queryWrapper);
            if (CollUtil.isEmpty(historyList)) {
                return 0;
            }
            // 反转列表，确保按时间正序（老的在前，新的在后）
            historyList = historyList.reversed();
            // 按时间顺序添加到记忆中
            int loadedCount = 0;
            // 走这里说明记忆已为空，清一次作为兜底，保证记忆与数据库完全一致
            chatMemory.clear();
            for (ChatHistory history : historyList) {
                if (ChatHistoryMessageTypeEnum.USER.getValue().equals(history.getMessageType())) {
                    chatMemory.add(UserMessage.from(history.getMessage()));
                    loadedCount++;
                } else if (ChatHistoryMessageTypeEnum.AI.getValue().equals(history.getMessageType())) {
                    chatMemory.add(AiMessage.from(history.getMessage()));
                    loadedCount++;
                }
            }
            log.info("成功为 appId: {} 加载了 {} 条历史对话", appId, loadedCount);
            return loadedCount;
        } catch (Exception e) {
            log.error("加载历史对话失败，appId: {}, error: {}", appId, e.getMessage(), e);
            // 加载失败不影响系统运行，只是没有历史上下文
            return 0;
        }
    }

    /**
     * 获取查询包装类
     *
     * @param chatHistoryQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (chatHistoryQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chatHistoryQueryRequest.getId();
        String message = chatHistoryQueryRequest.getMessage();
        String messageType = chatHistoryQueryRequest.getMessageType();
        Long appId = chatHistoryQueryRequest.getAppId();
        Long userId = chatHistoryQueryRequest.getUserId();
        LocalDateTime lastCreateTime = chatHistoryQueryRequest.getLastCreateTime();
        String sortField = chatHistoryQueryRequest.getSortField();
        String sortOrder = chatHistoryQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.eq("id", id)
                .like("message", message)
                .eq("messageType", messageType)
                .eq("appId", appId)
                .eq("userId", userId);
        // 游标查询逻辑 - 只使用 createTime 作为游标
        if (lastCreateTime != null) {
            queryWrapper.lt("createTime", lastCreateTime);
        }
        // 排序
        if (StrUtil.isNotBlank(sortField)) {
            queryWrapper.orderBy(sortField, "ascend".equals(sortOrder));
        } else {
            // 默认按创建时间降序排列
            queryWrapper.orderBy("createTime", false);
        }
        return queryWrapper;
    }
}
