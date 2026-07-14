package com.pims.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 住户信息更新DTO
 */
@Data
public class ResidentUpdateDTO {
    
    /**
     * 住户ID
     */
    @NotNull(message = "住户ID不能为空")
    private Long id;
    
    /**
     * 物业费单价
     */
    private BigDecimal pricePerSqm;
    
    /**
     * 入住状态：0未入住，1已入住
     */
    private Integer status;
}
