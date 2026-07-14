package com.pims.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 入住申请DTO
 */
@Data
public class ApplyResidentDTO {
    
    @NotBlank(message = "姓名不能为空")
    private String name;
    
    @NotBlank(message = "身份证号不能为空")
    private String idCard;
    
    @NotNull(message = "住房面积不能为空")
    private BigDecimal area;
}
