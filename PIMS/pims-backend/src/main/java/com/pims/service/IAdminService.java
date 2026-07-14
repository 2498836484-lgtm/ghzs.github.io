package com.pims.service;

import com.pims.dto.AuditDTO;
import com.pims.dto.LoginDTO;
import com.pims.entity.AuditRecord;
import com.pims.vo.LoginVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 管理员Service接口
 */
public interface IAdminService {
    
    /**
     * 管理员登录
     */
    LoginVO login(LoginDTO dto);
    
    /**
     * 获取待审核列表
     */
    Page<AuditRecord> getPendingList(Integer pageNum, Integer pageSize);
    
    /**
     * 审核入住申请
     */
    void processAudit(AuditDTO dto, Long adminId);
}
