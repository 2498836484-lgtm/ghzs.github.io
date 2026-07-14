package com.pims.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pims.entity.PaymentRecord;
import com.pims.entity.Resident;
import com.pims.mapper.PaymentRecordMapper;
import com.pims.mapper.ResidentMapper;
import com.pims.service.IMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 缴费提醒定时任务
 */
@Slf4j
@Component
public class PaymentReminderTask {

    @Autowired
    private ResidentMapper residentMapper;

    @Autowired
    private PaymentRecordMapper paymentRecordMapper;

    @Autowired
    private IMessageService messageService;

    /**
     * 每天凌晨1点检查未缴费用户，发送提醒消息
     * cron表达式：秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkAndSendPaymentReminder() {
        log.info("==========开始执行缴费提醒任务==========");
        
        try {
            // 获取当前月份（格式：yyyy-MM）
            String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            // 查询所有已入住的住户
            List<Resident> residents = residentMapper.selectList(
                new LambdaQueryWrapper<Resident>()
                    .eq(Resident::getStatus, 1)  // 已入住
            );
            
            log.info("当前月份：{}，已入住住户数：{}", currentMonth, residents.size());
            
            int reminderCount = 0;
            
            for (Resident resident : residents) {
                // 检查该住户本月是否已缴费
                PaymentRecord paymentRecord = paymentRecordMapper.selectOne(
                    new LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getRoomNumber, resident.getRoomNumber())
                        .eq(PaymentRecord::getPaymentMonth, currentMonth)
                        .last("LIMIT 1")
                );
                
                // 如果未缴费，发送提醒
                if (paymentRecord == null) {
                    String content = String.format(
                        "尊敬的住户，您的房号%s本月（%s）物业费还未缴纳，应缴金额：%.2f元。请及时缴纳，谢谢！",
                        resident.getRoomNumber(),
                        currentMonth,
                        resident.getArea().multiply(resident.getPricePerSqm()).doubleValue()
                    );
                    
                    messageService.sendPaymentReminder(resident.getUserId(), content);
                    reminderCount++;
                    
                    log.info("发送提醒：userId={}, roomNumber={}", resident.getUserId(), resident.getRoomNumber());
                }
            }
            
            log.info("==========缴费提醒任务执行完成，共发送{}\u6761提醒==========", reminderCount);
            
        } catch (Exception e) {
            log.error("缴费提醒任务执行失败", e);
        }
    }
    
    /**
     * 测试接口：手动触发缴费提醒（仅用于测试）
     * 可以通过调用此方法测试定时任务
     */
    public void manualTrigger() {
        log.info("手动触发缴费提醒任务");
        checkAndSendPaymentReminder();
    }
}
