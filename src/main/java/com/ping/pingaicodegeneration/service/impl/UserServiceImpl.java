package com.ping.pingaicodegeneration.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ping.pingaicodegeneration.model.entity.User;
import com.ping.pingaicodegeneration.mapper.UserMapper;
import com.ping.pingaicodegeneration.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author ping
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

}
