package com.ping.pingaicodegeneration;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class PingAiCodeScreenshotApplication {
    public static void main(String[] args) {
        SpringApplication.run(PingAiCodeScreenshotApplication.class, args);
    }
}