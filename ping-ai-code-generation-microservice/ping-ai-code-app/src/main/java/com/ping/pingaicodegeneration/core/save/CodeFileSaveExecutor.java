package com.ping.pingaicodegeneration.core.save;

import com.ping.pingaicodegeneration.ai.model.HtmlCodeResult;
import com.ping.pingaicodegeneration.ai.model.MultiFileCodeResult;
import com.ping.pingaicodegeneration.exception.BusinessException;
import com.ping.pingaicodegeneration.exception.ErrorCode;
import com.ping.pingaicodegeneration.model.enums.CodeGenTypeEnum;

import java.io.File;

public class CodeFileSaveExecutor {

    private static final HtmlCodeFileSaverTemplate htmlCodeFileSaver = new HtmlCodeFileSaverTemplate();
    private static final MultiFileCodeFileSaverTemplate multiFileCodeSaver = new MultiFileCodeFileSaverTemplate();

    /**
     * 执行代码保存
     *
     * @param codeResult  代码结果对象
     * @param codeGenType 代码生成类型
     * @return 保存的目录
     */
    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType,Long appId){
        return switch (codeGenType) {
            case HTML -> htmlCodeFileSaver.saveCode((HtmlCodeResult) codeResult, appId);
            case MULTI_FILE -> multiFileCodeSaver.saveCode((MultiFileCodeResult) codeResult, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型:"+ codeGenType);
        };
    }
}
