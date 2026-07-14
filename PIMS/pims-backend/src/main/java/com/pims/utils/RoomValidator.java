package com.pims.utils;

import java.util.regex.Pattern;

/**
 * 房号验证工具类
 */
public class RoomValidator {
    
    // 房号格式：Xxxx（如A310）
    private static final Pattern ROOM_PATTERN = Pattern.compile("^[A-Z]\\d{3}$");
    
    /**
     * 验证房号格式是否正确
     */
    public static boolean isValid(String roomNumber) {
        if (roomNumber == null || roomNumber.isEmpty()) {
            return false;
        }
        return ROOM_PATTERN.matcher(roomNumber).matches();
    }
}
