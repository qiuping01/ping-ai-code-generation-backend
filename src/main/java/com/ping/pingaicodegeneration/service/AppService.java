package com.ping.pingaicodegeneration.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.ping.pingaicodegeneration.model.dto.app.AppQueryRequest;
import com.ping.pingaicodegeneration.model.entity.App;
import com.ping.pingaicodegeneration.model.vo.AppVO;

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


}
