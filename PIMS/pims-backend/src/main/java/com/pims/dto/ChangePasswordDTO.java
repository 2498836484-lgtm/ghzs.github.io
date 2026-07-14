package com.pims.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改密码DTO
 */
@Data
public class ChangePasswordDTO {
    
    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    /**
     * 原密码
     */
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;
    
    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
    
    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
