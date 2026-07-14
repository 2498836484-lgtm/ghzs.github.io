package com.pims.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO {
    
    @NotBlank(message = "手机号不能为空")
    private String phone;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    @NotBlank(message = "房号不能为空")
    private String roomNumber;
    
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;
}
