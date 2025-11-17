package com.ping.pingaicodegeneration.service.impl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ping.pingaicodegeneration.common.ResultUtils;
import com.ping.pingaicodegeneration.exception.BusinessException;
import com.ping.pingaicodegeneration.exception.ErrorCode;
import com.ping.pingaicodegeneration.exception.ThrowUtils;
import com.ping.pingaicodegeneration.model.entity.User;
import com.ping.pingaicodegeneration.mapper.UserMapper;
import com.ping.pingaicodegeneration.model.enums.UserRoleEnum;
import com.ping.pingaicodegeneration.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

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
}
