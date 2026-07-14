package com.pims.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 审核请求DTO
 */
@Data
public class AuditDTO {
    
    @NotNull(message = "审核记录ID不能为空")
    private Long auditId;
    
    @NotNull(message = "审核状态不能为空")
    private Integer status;  // 1通过，2拒绝
    
    private BigDecimal pricePerSqm;  // 物业费单价
}
