package com.ping.pingaicodegeneration.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ping.pingaicodegeneration.model.entity.App;
import com.ping.pingaicodegeneration.mapper.AppMapper;
import com.ping.pingaicodegeneration.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author ping
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

}
