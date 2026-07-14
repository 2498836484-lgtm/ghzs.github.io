package com.pims.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 管理员实体
 */
@Data
@TableName("t_admin")
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private Integer isSuperAdmin;  // 0否，1是
    
    private Date createdTime;
    
    private Date updatedTime;
}
