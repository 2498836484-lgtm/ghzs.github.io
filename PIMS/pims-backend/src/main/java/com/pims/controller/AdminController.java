package com.pims.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pims.common.Result;
import com.pims.dto.AuditDTO;
import com.pims.dto.LoginDTO;
import com.pims.dto.PaymentQueryDTO;
import com.pims.entity.AuditRecord;
import com.pims.service.IAdminService;
import com.pims.service.IPaymentService;
import com.pims.vo.LoginVO;
import com.pims.vo.PaymentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员Controller
 */
@Slf4j
@Api(tags = "管理员管理")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private IAdminService adminService;

    @Autowired
    private IPaymentService paymentService;

    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO dto) {
        LoginVO vo = adminService.login(dto);
        return Result.success(vo);
    }

    @ApiOperation("获取待审核列表")
    @GetMapping("/audit/pending-list")
    public Result<Page<AuditRecord>> getPendingList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<AuditRecord> page = adminService.getPendingList(pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("审核入住申请")
    @PostMapping("/audit/process")
    public Result<?> processAudit(@Validated @RequestBody AuditDTO dto,
                                   @RequestParam Long adminId) {
        adminService.processAudit(dto, adminId);
        return Result.success("审核成功");
    }

    @ApiOperation("管理员查询所有缴费记录")
    @GetMapping("/payment/list")
    public Result<IPage<PaymentVO>> getAllPaymentList(PaymentQueryDTO queryDTO) {
        IPage<PaymentVO> page = paymentService.getAllPaymentList(queryDTO);
        return Result.success(page);
    }
}
