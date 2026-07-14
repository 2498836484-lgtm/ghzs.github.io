package com.pims.utils;

import java.util.regex.Pattern;

/**
 * 身份证号验证工具
 */
public class IdCardValidator {

    private static final Pattern ID_CARD_PATTERN = 
        Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");

    /**
     * 验证身份证号格式
     */
    public static boolean isValid(String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return false;
        }
        return ID_CARD_PATTERN.matcher(idCard).matches();
    }
}
