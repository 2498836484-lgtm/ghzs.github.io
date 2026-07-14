package com.pims.vo;

import lombok.Data;

/**
 * 登录返回VO
 */
@Data
public class LoginVO {
    private String token;
    private UserInfoVO userInfo;
}
