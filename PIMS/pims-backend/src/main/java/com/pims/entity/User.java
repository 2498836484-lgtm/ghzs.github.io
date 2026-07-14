package com.pims.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体
 */
@Data
@TableName("t_user")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String phone;
    
    private String password;
    
    private String roomNumber;
    
    private Integer status;  // 0未入住，1已入住
    
    private Date createdTime;
    
    private Date updatedTime;
    
    @TableLogic
    private Integer isDeleted;  // 0未删除，1已删除
}
