package com.pims.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 缴费记录视图对象
 */
@Data
public class PaymentVO {
    
    private Long id;
    
    private String roomNumber;
    
    private BigDecimal area;
    
    private BigDecimal pricePerSqm;
    
    private BigDecimal amount;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentTime;
    
    private String paymentMonth;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdTime;
}
