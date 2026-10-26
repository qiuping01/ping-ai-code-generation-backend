package com.ping.pingaicodegeneration;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.ping.pingaicodegeneration.mapper")
public class PingAiCodeGenerationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PingAiCodeGenerationApplication.class, args);
    }
}
