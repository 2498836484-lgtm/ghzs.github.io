package com.pims.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 缴费记录实体
 */
@Data
@TableName("t_payment_record")
public class PaymentRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String roomNumber;
    
    private BigDecimal area;
    
    private BigDecimal pricePerSqm;
    
    private BigDecimal amount;
    
    private Date paymentTime;
    
    private String paymentMonth;
    
    private Date createdTime;
}
