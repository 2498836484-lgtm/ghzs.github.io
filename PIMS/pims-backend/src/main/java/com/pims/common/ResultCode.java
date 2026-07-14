package com.pims.common;

import lombok.Getter;

/**
 * 返回码枚举
 * 
 * @author PIMS
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "success"),
    ERROR(500, "服务器内部错误"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未认证/Token失效"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    
    // 业务错误码 1001-1999
    VERIFY_CODE_ERROR(1001, "验证码错误"),
    USERNAME_OR_PASSWORD_ERROR(1002, "用户名或密码错误"),
    ROOM_NUMBER_EXISTS(1003, "房号已存在"),
    PHONE_EXISTS(1004, "手机号已注册"),
    AUDIT_PENDING(1005, "审核中不能重复申请"),
    UNPAID_FEE(1006, "物业费未缴清，不能退房"),
    USER_NOT_FOUND(1007, "用户不存在"),
    USER_DELETED(1008, "用户已注销"),
    ADMIN_EXISTS(1009, "管理员用户名已存在"),
    NO_PERMISSION(1010, "无权限修改该管理员信息");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
