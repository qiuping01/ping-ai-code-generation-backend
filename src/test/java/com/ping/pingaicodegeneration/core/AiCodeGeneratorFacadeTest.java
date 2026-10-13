package com.ping.pingaicodegeneration.core;

import com.ping.pingaicodegeneration.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeGeneratorFacadeTest {

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generateAndSaveCode_HTML() {
        File file = aiCodeGeneratorFacade.generateAndSaveCode
                ("做一个程序员阿平博客，代码不超过20行", CodeGenTypeEnum.HTML);
        Assertions.assertNotNull(file);
    }

    @Test
    void generateAndSaveCode_MULTI() {
        File file = aiCodeGeneratorFacade.generateAndSaveCode
                ("做一个程序员阿平博客，尽量用最少行数的代码实现", CodeGenTypeEnum.MULTI_FILE);
        Assertions.assertNotNull(file);
    }

}