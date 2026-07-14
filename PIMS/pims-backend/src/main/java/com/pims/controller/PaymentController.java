package com.pims.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pims.common.Result;
import com.pims.dto.PaymentDTO;
import com.pims.dto.PaymentQueryDTO;
import com.pims.service.IPaymentService;
import com.pims.utils.JwtUtil;
import com.pims.vo.PaymentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 缴费管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/payment")
@Api(tags = "缴费管理")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户提交缴费记录
     */
    @PostMapping("/submit")
    @ApiOperation("提交缴费记录")
    public Result<Void> submitPayment(@Valid @RequestBody PaymentDTO dto, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        paymentService.submitPayment(dto, userId);
        return Result.success();
    }

    /**
     * 用户查询自己的缴费记录
     */
    @GetMapping("/list")
    @ApiOperation("查询缴费记录")
    public Result<IPage<PaymentVO>> getPaymentList(PaymentQueryDTO queryDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        IPage<PaymentVO> page = paymentService.getPaymentList(queryDTO, userId);
        return Result.success(page);
    }

    /**
     * 查询缴费记录详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperation("查询缴费记录详情")
    public Result<PaymentVO> getPaymentDetail(@PathVariable Long id) {
        PaymentVO vo = paymentService.getPaymentById(id);
        return Result.success(vo);
    }
}
