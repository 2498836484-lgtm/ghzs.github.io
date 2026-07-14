package com.pims.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pims.dto.PaymentDTO;
import com.pims.dto.PaymentQueryDTO;
import com.pims.vo.PaymentVO;

/**
 * 缴费管理服务接口
 */
public interface IPaymentService {
    
    /**
     * 提交缴费记录
     * @param dto 缴费信息
     * @param userId 用户ID
     */
    void submitPayment(PaymentDTO dto, Long userId);
    
    /**
     * 分页查询缴费记录
     * @param queryDTO 查询条件
     * @param userId 用户ID
     * @return 缴费记录分页列表
     */
    IPage<PaymentVO> getPaymentList(PaymentQueryDTO queryDTO, Long userId);
    
    /**
     * 管理员查询所有缴费记录
     * @param queryDTO 查询条件
     * @return 缴费记录分页列表
     */
    IPage<PaymentVO> getAllPaymentList(PaymentQueryDTO queryDTO);
    
    /**
     * 根据ID查询缴费记录详情
     * @param id 记录ID
     * @return 缴费记录详情
     */
    PaymentVO getPaymentById(Long id);
}
