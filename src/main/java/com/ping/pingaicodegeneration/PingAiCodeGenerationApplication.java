package com.ping.pingaicodegeneration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ping.pingaicodegeneration.mapper")
public class PingAiCodeGenerationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PingAiCodeGenerationApplication.class, args);
    }
}
