package com.pims.controller;

import com.pims.common.Result;
import com.pims.task.PaymentReminderTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务控制器（测试用）
 */
@Slf4j
@RestController
@RequestMapping("/task")
@Api(tags = "定时任务管理（测试）")
public class TaskController {

    @Autowired
    private PaymentReminderTask paymentReminderTask;

    /**
     * 手动触发缴费提醒任务（测试）
     */
    @PostMapping("/payment-reminder")
    @ApiOperation("手动触发缴费提醒任务")
    public Result<?> triggerPaymentReminder() {
        log.info("手动触发缴费提醒任务");
        paymentReminderTask.manualTrigger();
        return Result.success("缴费提醒任务执行成功");
    }
}
