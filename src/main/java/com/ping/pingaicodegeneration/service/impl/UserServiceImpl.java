package com.ping.pingaicodegeneration.service.impl;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ping.pingaicodegeneration.exception.ErrorCode;
import com.ping.pingaicodegeneration.exception.ThrowUtils;
import com.ping.pingaicodegeneration.mapper.UserMapper;
import com.ping.pingaicodegeneration.model.entity.User;
import com.ping.pingaicodegeneration.model.enums.UserRoleEnum;
import com.ping.pingaicodegeneration.model.vo.LoginUserVO;
import com.ping.pingaicodegeneration.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static com.ping.pingaicodegeneration.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户 服务层实现。
 *
 * @author ping
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  密码
     * @param checkPassword 确认密码
     * @return 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword)
                , ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(userAccount.length() < 4
                , ErrorCode.PARAMS_ERROR, "用户账户过短");
        ThrowUtils.throwIf(userPassword.length() < 8
                , ErrorCode.PARAMS_ERROR, "密码过短");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword)
                , ErrorCode.PARAMS_ERROR, "两次密码不一致");
        // 2. 检查用户账户是否和数据库已有的重复
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        long countByQuery = this.mapper.selectCountByQuery(queryWrapper);
        ThrowUtils.throwIf(countByQuery > 0
                , ErrorCode.PARAMS_ERROR, "用户账户已存在");
        // 3. 用户密码加密
        String encryptPassword = this.getEncryptPassword(userPassword);
        // 4. 生成6位UUID用户名
        String userName = "用户" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        // 5. 保存用户信息到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setUserName(userName);
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
        return user.getId(); // 主键回传
    }

    /**
     * 获取加密后的密码
     *
     * @param userPassword 用户密码
     * @return 加密后的密码
     */
    @Override
    public String getEncryptPassword(String userPassword) {
        // 1. 加盐，混淆密码
        final String SALT = "ping";
        // 2. 使用单向加密
        return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取脱敏登录用户信息
     *
     * @param user 用户
     * @return 脱敏用户信息
     */
    @Override
    public LoginUserVO getLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    /**
     * 获取脱敏登录用户信息
     *
     * @param request 请求
     * @return  用户信息
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断用户是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        ThrowUtils.throwIf(currentUser == null || currentUser.getId() == null
                , ErrorCode.NOT_LOGIN_ERROR);
        // 从数据库查询当前用户信息
        long userId = currentUser.getId();
        currentUser = this.getById(userId);
        ThrowUtils.throwIf(currentUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return currentUser;
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      请求
     * @return 脱敏后的登录用户信息
     */
    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验参数
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword)
                , ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(userAccount.length() < 4
                , ErrorCode.PARAMS_ERROR, "用户账户过短");
        ThrowUtils.throwIf(userPassword.length() < 8
                , ErrorCode.PARAMS_ERROR, "密码过短");
        // 2. 加密
        String encryptPassword = getEncryptPassword(userPassword);
        // 3. 查询用户信息是否存在
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = this.mapper.selectOneByQuery(queryWrapper);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR
                , "用户不存在或密码错误");
        // 4. 如果用户存在，记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        // 5. 返回脱敏后的用户信息
        return this.getLoginUserVO(user);
    }
}
