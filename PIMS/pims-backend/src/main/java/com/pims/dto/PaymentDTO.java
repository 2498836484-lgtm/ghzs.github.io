package com.pims.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 缴费数据传输对象
 */
@Data
public class PaymentDTO {
    
    @NotBlank(message = "缴费月份不能为空")
    private String paymentMonth;  // 格式：2024-01
    
    @NotNull(message = "缴费金额不能为空")
    private BigDecimal amount;
    
    private String roomNumber;
    
    private BigDecimal area;
    
    private BigDecimal pricePerSqm;
}
