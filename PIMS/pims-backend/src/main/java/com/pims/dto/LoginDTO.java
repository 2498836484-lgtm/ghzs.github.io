package com.pims.dto;

import lombok.Data;

@Data
public class LoginDTO {
    private String account;  // 账号（房号或手机号或用户名）
    private String password; // 密码
    private String username; // 管理员用户名
}
