package com.pims.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pims.dto.PaymentQueryDTO;
import com.pims.vo.MessageVO;

/**
 * 消息管理服务接口
 */
public interface IMessageService {
    
    /**
     * 获取用户未读消息数量
     * @param userId 用户ID
     * @return 未读消息数量
     */
    Integer getUnreadCount(Long userId);
    
    /**
     * 分页获取用户消息列表
     * @param queryDTO 查询条件
     * @param userId 用户ID
     * @return 消息分页列表
     */
    IPage<MessageVO> getMessageList(PaymentQueryDTO queryDTO, Long userId);
    
    /**
     * 标记消息为已读
     * @param id 消息ID
     * @param userId 用户ID
     */
    void markAsRead(Long id, Long userId);
    
    /**
     * 标记所有消息为已读
     * @param userId 用户ID
     */
    void markAllAsRead(Long userId);
    
    /**
     * 发送缴费提醒消息（系统调用）
     * @param userId 用户ID
     * @param content 消息内容
     */
    void sendPaymentReminder(Long userId, String content);
    
    /**
     * 缴费后清除本月的未读缴费提醒消息
     * @param userId 用户ID
     * @param paymentMonth 缴费月份（格式：yyyy-MM）
     */
    void clearPaymentReminderByMonth(Long userId, String paymentMonth);
}
