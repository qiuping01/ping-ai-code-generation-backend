package com.ping.pingaicodegeneration.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ping.pingaicodegeneration.model.dto.app.AppQueryRequest;
import com.ping.pingaicodegeneration.model.entity.App;
import com.ping.pingaicodegeneration.model.entity.User;
import com.ping.pingaicodegeneration.model.vo.AppVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author ping
 */
public interface AppService extends IService<App> {

    /**
     * 获取应用封装类
     *
     * @param app
     * @return
     */
    AppVO getAppVO(App app);

    /**
     * 获取应用封装类列表
     *
     * @param appList
     * @return
     */
    List<AppVO> getAppVOList(List<App> appList);

    /**
     * 构造应用查询条件
     * 该方法根据传入的查询请求对象，构建一个QueryWrapper对象，用于数据库查询条件的设置

     *
     * @param appQueryRequest 应用的查询请求对象，包含查询所需的各项条件参数
     * @return QueryWrapper 构建好的查询条件对象，可用于MyBatis-Plus的查询操作
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);


    /**
     * 生成代码的聊天方法
     *
     * @param appId      应用ID，用于标识具体的应用
     * @param userPrompt 用户输入的提示信息，用于生成代码的输入
     * @param loginUser  当前登录用户信息，用于权限验证和用户上下文
     * @return 返回一个Flux<String>类型的响应流，包含生成的代码内容
     */
    Flux<String> chatToGenCode(Long appId, String userPrompt, User loginUser);

}
