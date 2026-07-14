package com.pims.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pims.exception.BusinessException;
import com.pims.common.ResultCode;
import com.pims.dto.PaymentQueryDTO;
import com.pims.entity.Message;
import com.pims.mapper.MessageMapper;
import com.pims.service.IMessageService;
import com.pims.vo.MessageVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息管理服务实现类
 */
@Slf4j
@Service
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public Integer getUnreadCount(Long userId) {
        log.info("获取未读消息数量，userId: {}", userId);
        
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
               .eq(Message::getIsRead, 0);
        
        return messageMapper.selectCount(wrapper).intValue();
    }

    @Override
    public IPage<MessageVO> getMessageList(PaymentQueryDTO queryDTO, Long userId) {
        log.info("获取用户消息列表，userId: {}, pageNum: {}", userId, queryDTO.getPageNum());
        
        Page<Message> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
               .orderByDesc(Message::getCreatedTime);
        
        IPage<Message> messagePage = messageMapper.selectPage(page, wrapper);
        
        // 转换为VO
        List<MessageVO> voList = messagePage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        Page<MessageVO> voPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long id, Long userId) {
        log.info("标记消息为已读，id: {}, userId: {}", id, userId);
        
        Message message = messageMapper.selectById(id);
        if (message == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "消息不存在");
        }
        
        if (!message.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        
        message.setIsRead(1);
        messageMapper.updateById(message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        log.info("标记所有消息为已读，userId: {}", userId);
        
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
               .eq(Message::getIsRead, 0);
        
        List<Message> messageList = messageMapper.selectList(wrapper);
        
        messageList.forEach(message -> {
            message.setIsRead(1);
            messageMapper.updateById(message);
        });
        
        log.info("成功标记{}条消息为已读", messageList.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendPaymentReminder(Long userId, String content) {
        log.info("发送缴费提醒消息，userId: {}", userId);
        
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle("缴费提醒");
        message.setContent(content);
        message.setIsRead(0);
        message.setMessageType(1);
        message.setCreatedTime(new Date());
        
        messageMapper.insert(message);
        log.info("缴费提醒消息发送成功，messageId: {}", message.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearPaymentReminderByMonth(Long userId, String paymentMonth) {
        log.info("清除缴费提醒消息，userId: {}, month: {}", userId, paymentMonth);
        
        // 查询该用户本月的未读缴费提醒消息
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getUserId, userId)
               .eq(Message::getMessageType, 1)  // 缴费提醒
               .eq(Message::getIsRead, 0)  // 未读
               .like(Message::getContent, paymentMonth);  // 包含该月份
        
        List<Message> messages = messageMapper.selectList(wrapper);
        
        // 标记为已读
        for (Message message : messages) {
            message.setIsRead(1);
            messageMapper.updateById(message);
        }
        
        log.info("成功清除{}\u6761缴费提醒消息", messages.size());
    }

    /**
     * 实体转VO
     */
    private MessageVO convertToVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtil.copyProperties(message, vo);
        return vo;
    }
}
