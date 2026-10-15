package com.ping.pingaicodegeneration.core.save;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.ping.pingaicodegeneration.exception.BusinessException;
import com.ping.pingaicodegeneration.exception.ErrorCode;
import com.ping.pingaicodegeneration.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 抽象代码文件保存器 - 模版方法模式
 */
public abstract class CodeFileSaverTemplate<T> {

    // 文件保存根目录
    protected static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 模板方法：保存代码的标准流程（使用 appId）
     *
     * @param codeResult 代码结果对象
     * @param appId  应用 ID
     * @return 保存的目录
     */
    public final File saveCode(T codeResult,Long appId) {
        // 1. 验证输入
        validateInput(codeResult);
        // 2. 构建唯一目录路径
        String baseDirPath = buildUniqueDir(appId);
        // 3. 保存文件（具体实现由子类提供）
        saveFiles(codeResult, baseDirPath);
        // 4. 返回文件对象
        return new File(baseDirPath);
    }

    /**
     * 获取代码类型（由子类实现）
     *
     * @return 代码生成类型
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 保存文件的具体实现（由子类实现）
     *
     * @param codeResult      代码结果对象
     * @param baseDirPath 基础目录路径
     */
    protected abstract void saveFiles(T codeResult, String baseDirPath);

    /**
     * 验证输入参数（可由子类覆盖）
     *
     * @param codeResult 代码结果对象
     */
    protected void validateInput(T codeResult){
        if (codeResult == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码结果对象不能为空");
        }
    }

    /**
     * 构建基于 appId 的目录路径
     *
     * @param appId 应用 ID
     * @return 目录路径
     */
    protected final String buildUniqueDir(Long appId) {
        if (appId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        }
        String codeType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", codeType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件
     */
    protected final void writeToFile(String dirPath, String filename, String content) {
        // 内容为空说明 AI 输出缺少该代码块，直接抛出便于定位，避免写入空文件或后续空指针
        if (StrUtil.isBlank(content)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码内容为空，无法保存文件：" + filename);
        }
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}
