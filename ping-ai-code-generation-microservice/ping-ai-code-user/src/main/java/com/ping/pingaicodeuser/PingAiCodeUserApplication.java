package com.ping.pingaicodeuser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.ping.pingaicodeuser.mapper")
@ComponentScan("com.ping")
public class PingAiCodeUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(PingAiCodeUserApplication.class, args);
    }
}