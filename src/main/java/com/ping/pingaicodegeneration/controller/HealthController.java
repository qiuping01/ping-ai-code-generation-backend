package com.ping.pingaicodegeneration.controller;

import com.ping.pingaicodegeneration.common.BaseResponse;
import com.ping.pingaicodegeneration.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/")
    public BaseResponse<String> healthCheck() {
        return ResultUtils.success("PingAi Code Generation is running");
    }
}