package com.pims.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pims.exception.BusinessException;
import com.pims.common.ResultCode;
import com.pims.dto.PaymentDTO;
import com.pims.dto.PaymentQueryDTO;
import com.pims.entity.PaymentRecord;
import com.pims.entity.Resident;
import com.pims.entity.User;
import com.pims.mapper.PaymentRecordMapper;
import com.pims.mapper.ResidentMapper;
import com.pims.mapper.UserMapper;
import com.pims.service.IMessageService;
import com.pims.service.IPaymentService;
import com.pims.vo.PaymentVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 缴费管理服务实现类
 */
@Slf4j
@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResidentMapper residentMapper;

    @Autowired
    private IMessageService messageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitPayment(PaymentDTO dto, Long userId) {
        log.info("用户提交缴费记录，userId: {}, 缴费月份: {}", userId, dto.getPaymentMonth());

        // 1. 验证用户状态
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "用户未入住，无法缴费");
        }

        // 2. 获取住户信息
        LambdaQueryWrapper<Resident> residentWrapper = new LambdaQueryWrapper<>();
        residentWrapper.eq(Resident::getUserId, userId);
        Resident resident = residentMapper.selectOne(residentWrapper);
        if (resident == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "未找到住户信息");
        }

        // 3. 检查该月份是否已缴费
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getUserId, userId)
               .eq(PaymentRecord::getPaymentMonth, dto.getPaymentMonth());
        PaymentRecord existRecord = paymentRecordMapper.selectOne(wrapper);
        if (existRecord != null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), 
                    "该月份已有缴费记录，请勿重复缴费");
        }

        // 4. 创建缴费记录
        PaymentRecord record = new PaymentRecord();
        record.setUserId(userId);
        record.setRoomNumber(dto.getRoomNumber() != null ? dto.getRoomNumber() : user.getRoomNumber());
        record.setArea(dto.getArea() != null ? dto.getArea() : resident.getArea());
        record.setPricePerSqm(dto.getPricePerSqm() != null ? dto.getPricePerSqm() : resident.getPricePerSqm());
        record.setAmount(dto.getAmount());
        record.setPaymentMonth(dto.getPaymentMonth());
        record.setPaymentTime(new Date());
        record.setCreatedTime(new Date());

        paymentRecordMapper.insert(record);
        log.info("缴费记录创建成功，recordId: {}", record.getId());

        // 5. 清除该月份的未读缴费提醒消息
        messageService.clearPaymentReminderByMonth(userId, dto.getPaymentMonth());

        // 6. 发送缴费成功消息
        String content = String.format("您的%s月份物业费%s元已缴纳成功", 
                dto.getPaymentMonth(), dto.getAmount());
        messageService.sendPaymentReminder(userId, content);
    }

    @Override
    public IPage<PaymentVO> getPaymentList(PaymentQueryDTO queryDTO, Long userId) {
        log.info("查询用户缴费记录，userId: {}, pageNum: {}", userId, queryDTO.getPageNum());

        Page<PaymentRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getUserId, userId);
        
        if (queryDTO.getPaymentMonth() != null && !queryDTO.getPaymentMonth().isEmpty()) {
            wrapper.eq(PaymentRecord::getPaymentMonth, queryDTO.getPaymentMonth());
        }
        
        wrapper.orderByDesc(PaymentRecord::getCreatedTime);

        IPage<PaymentRecord> recordPage = paymentRecordMapper.selectPage(page, wrapper);
        
        // 转换为VO
        List<PaymentVO> voList = recordPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Page<PaymentVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public IPage<PaymentVO> getAllPaymentList(PaymentQueryDTO queryDTO) {
        log.info("管理员查询所有缴费记录，pageNum: {}", queryDTO.getPageNum());

        Page<PaymentRecord> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (queryDTO.getPaymentMonth() != null && !queryDTO.getPaymentMonth().isEmpty()) {
            wrapper.eq(PaymentRecord::getPaymentMonth, queryDTO.getPaymentMonth());
        }
        
        wrapper.orderByDesc(PaymentRecord::getCreatedTime);

        IPage<PaymentRecord> recordPage = paymentRecordMapper.selectPage(page, wrapper);
        
        // 转换为VO
        List<PaymentVO> voList = recordPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        Page<PaymentVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        voPage.setRecords(voList);
        
        return voPage;
    }

    @Override
    public PaymentVO getPaymentById(Long id) {
        log.info("查询缴费记录详情，id: {}", id);
        
        PaymentRecord record = paymentRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "缴费记录不存在");
        }
        
        return convertToVO(record);
    }

    /**
     * 实体转VO
     */
    private PaymentVO convertToVO(PaymentRecord record) {
        PaymentVO vo = new PaymentVO();
        BeanUtil.copyProperties(record, vo);
        return vo;
    }
}
