package com.ping.pingaicodegeneration.core.save;

import cn.hutool.core.util.StrUtil;
import com.ping.pingaicodegeneration.ai.model.HtmlCodeResult;
import com.ping.pingaicodegeneration.exception.BusinessException;
import com.ping.pingaicodegeneration.exception.ErrorCode;
import com.ping.pingaicodegeneration.model.enums.CodeGenTypeEnum;

public class HtmlCodeFileSaverTemplate extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(HtmlCodeResult codeResult, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", codeResult.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult codeResult) {
        super.validateInput(codeResult);
        // HTML 代码不能为空
        if (StrUtil.isBlank(codeResult.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "HTML代码内容不能为空");
        }
    }
}
