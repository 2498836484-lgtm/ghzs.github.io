package com.pims.dto;

import lombok.Data;

/**
 * 缴费记录查询DTO
 */
@Data
public class PaymentQueryDTO {
    
    private String paymentMonth;  // 查询指定月份
    
    private Integer pageNum = 1;
    
    private Integer pageSize = 10;
}
