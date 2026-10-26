package com.ping.pingaicodegeneration.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.ping.pingaicodegeneration.model.entity.User;

import java.time.LocalDateTime;


/**
 * 缓存 key 生成工具类
 *
 * @author ping
 */
public class CacheKeyUtils {

    /**
     * 根据对象生成缓存 key (JSON + MD5)
     *
     * @param obj 要生成 key 的对象
     * @return MD5 哈希后的缓存  key
     */
    public static String generateKey(Object obj) {
        if (obj == null) {
            // 缓存空值，避免缓存穿透
            return DigestUtil.md5Hex("null");
        }
        // 先转 JSON，再 MD5
        String jsonStr = JSONUtil.toJsonStr(obj);
        return DigestUtil.md5Hex(jsonStr);
    }
}

