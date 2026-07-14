package com.pims.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 住户信息实体
 */
@Data
@TableName("t_resident")
public class Resident implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String name;
    
    private String idCard;
    
    private String phone;
    
    private String roomNumber;
    
    private BigDecimal area;
    
    private BigDecimal pricePerSqm;
    
    private Integer status;  // 0未入住，1已入住
    
    private Date registerTime;
    
    private Date createdTime;
    
    private Date updatedTime;
}
