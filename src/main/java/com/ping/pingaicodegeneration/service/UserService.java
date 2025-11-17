package com.ping.pingaicodegeneration.service;

import com.mybatisflex.core.service.IService;
import com.ping.pingaicodegeneration.model.entity.User;

/**
 * 用户 服务层。
 *
 * @author ping
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount 用户账号
     * @param password 密码
     * @param checkPassword 确认密码
     * @return 用户ID
     */
    long userRegister(String userAccount, String password, String checkPassword);

    /**
     * 获取加密后的密码
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    String getEncryptPassword(String userPassword);
}
