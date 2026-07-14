package com.pims.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 验证码工具类（简化版，使用内存存储）
 */
@Slf4j
@Component
public class VerifyCodeUtil {

    // 验证码存储（生产环境应使用Redis）
    private static final Map<String, CodeInfo> CODE_MAP = new ConcurrentHashMap<>();

    /**
     * 生成6位数字验证码
     */
    public String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    /**
     * 发送验证码（模拟，打印到日志）
     */
    public void sendCode(String phone, String code) {
        log.info("========== 验证码发送 ==========");
        log.info("手机号：{}", phone);
        log.info("验证码：{}", code);
        log.info("有效期：5分钟");
        log.info("================================");

        // 存储验证码，5分钟有效期
        CODE_MAP.put(phone, new CodeInfo(code, System.currentTimeMillis() + 5 * 60 * 1000));
    }

    /**
     * 验证验证码
     */
    public boolean verify(String phone, String code) {
        CodeInfo codeInfo = CODE_MAP.get(phone);
        if (codeInfo == null) {
            return false;
        }

        // 检查是否过期
        if (System.currentTimeMillis() > codeInfo.getExpireTime()) {
            CODE_MAP.remove(phone);
            return false;
        }

        // 验证码正确，使用后删除
        if (codeInfo.getCode().equals(code)) {
            CODE_MAP.remove(phone);
            return true;
        }

        return false;
    }

    /**
     * 验证码信息内部类
     */
    private static class CodeInfo {
        private String code;
        private Long expireTime;

        public CodeInfo(String code, Long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }

        public String getCode() {
            return code;
        }

        public Long getExpireTime() {
            return expireTime;
        }
    }
}
