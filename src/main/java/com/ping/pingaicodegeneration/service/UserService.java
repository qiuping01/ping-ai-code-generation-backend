package com.ping.pingaicodegeneration.service;

import com.mybatisflex.core.service.IService;
import com.ping.pingaicodegeneration.model.entity.User;
import com.ping.pingaicodegeneration.model.vo.LoginUserVO;
import jakarta.servlet.http.HttpServletRequest;

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

    /**
     * 脱敏登录用户信息
     *
     * @param user  用户
     * @return  脱敏用户信息
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest  request);

    /**
     * 用户登录
     *
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param request 请求
     * @return 脱敏后的登录用户信息
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

}
