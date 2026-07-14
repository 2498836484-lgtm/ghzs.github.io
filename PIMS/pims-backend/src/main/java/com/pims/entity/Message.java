package com.pims.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统消息实体
 */
@Data
@TableName("t_message")
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String title;
    
    private String content;
    
    private Integer isRead;  // 0未读，1已读
    
    private Integer messageType;  // 1缴费提醒
    
    private Date createdTime;
}
