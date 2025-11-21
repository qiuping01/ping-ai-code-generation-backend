package com.ping.pingaicodegeneration.aop;

import com.ping.pingaicodegeneration.annotation.AuthCheck;
import com.ping.pingaicodegeneration.exception.ThrowUtils;
import com.ping.pingaicodegeneration.model.entity.User;
import com.ping.pingaicodegeneration.model.enums.UserRoleEnum;
import com.ping.pingaicodegeneration.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.ping.pingaicodegeneration.exception.ErrorCode.NO_AUTH_ERROR;

/**
 * 权限校验拦截器
 */
@Component
@Aspect
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行拦截
     *
     * @param joinPoint 切入点
     * @param authCheck 权限校验注解
     * @return 目标方法的执行结果
     * @throws Throwable 异常
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 1. 获取注解的值
        String mustRole = authCheck.mustRole();
        // 2. 获取当前登录用户
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);
        // 统一转换为枚举对象
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 3. 不需要权限，直接放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        // 4. 以下的代码：必须有权限才能通过
        // 没有权限，直接拒绝
        ThrowUtils.throwIf(userRoleEnum == null, NO_AUTH_ERROR);
        // 要求必须有管理员权限，但当前登录用户没有
        ThrowUtils.throwIf(UserRoleEnum.ADMIN.equals(mustRoleEnum) &&
                                    !UserRoleEnum.ADMIN.equals(userRoleEnum), NO_AUTH_ERROR);
        // 5. 权限校验通过，放行
        return joinPoint.proceed();
    }
}
