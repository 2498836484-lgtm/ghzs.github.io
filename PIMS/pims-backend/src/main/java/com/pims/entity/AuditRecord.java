package com.pims.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 入住审核记录实体
 */
@Data
@TableName("t_audit_record")
public class AuditRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String name;
    
    private String idCard;
    
    private BigDecimal area;
    
    private String roomNumber;
    
    private Integer status;  // 0待审核，1通过，2拒绝
    
    private Long adminId;
    
    private Date auditTime;
    
    private Date createdTime;
}
