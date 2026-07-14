package com.pims.vo;

import lombok.Data;

/**
 * 用户信息VO
 */
@Data
public class UserInfoVO {
    private Long id;
    private String phone;
    private String roomNumber;
    private Integer status;  // 0未入住，1已入住
}
