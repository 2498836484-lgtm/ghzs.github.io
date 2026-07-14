package com.pims.utils;

import java.util.regex.Pattern;

/**
 * 手机号验证工具
 */
public class PhoneValidator {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    /**
     * 验证手机号格式
     */
    public static boolean isValid(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
}
