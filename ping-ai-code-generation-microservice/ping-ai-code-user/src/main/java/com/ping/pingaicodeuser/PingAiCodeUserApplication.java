package com.ping.pingaicodeuser;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDubbo
@MapperScan("com.ping.pingaicodeuser.mapper")
@ComponentScan("com.ping")
public class PingAiCodeUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(PingAiCodeUserApplication.class, args);
    }
}