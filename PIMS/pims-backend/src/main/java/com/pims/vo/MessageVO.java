package com.pims.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 消息视图对象
 */
@Data
public class MessageVO {
    
    private Long id;
    
    private String title;
    
    private String content;
    
    private Integer isRead;  // 0未读，1已读
    
    private Integer messageType;  // 1缴费提醒
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdTime;
}
