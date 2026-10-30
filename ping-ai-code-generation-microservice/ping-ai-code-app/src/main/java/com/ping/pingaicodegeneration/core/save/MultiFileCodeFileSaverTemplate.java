package com.ping.pingaicodegeneration.core.save;

import cn.hutool.core.util.StrUtil;
import com.ping.pingaicodegeneration.ai.model.MultiFileCodeResult;
import com.ping.pingaicodegeneration.exception.BusinessException;
import com.ping.pingaicodegeneration.exception.ErrorCode;
import com.ping.pingaicodegeneration.model.enums.CodeGenTypeEnum;

public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(MultiFileCodeResult codeResult, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", codeResult.getHtmlCode());
        writeToFile(baseDirPath, "style.css", codeResult.getCssCode());
        writeToFile(baseDirPath, "script.js", codeResult.getJsCode());
    }

    @Override
    protected void validateInput(MultiFileCodeResult codeResult) {
        super.validateInput(codeResult);
        // 三个文件缺一不可：任一为空都说明模型没有按提示词的格式输出，
        // 需在写文件之前失败，避免留下只写了一部分的半成品目录
        if (StrUtil.isBlank(codeResult.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
        }
        if (StrUtil.isBlank(codeResult.getCssCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "CSS代码内容不能为空");
        }
        if (StrUtil.isBlank(codeResult.getJsCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "JavaScript代码内容不能为空");
        }
    }
}
