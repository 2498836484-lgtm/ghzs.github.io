package com.pims.service;

import com.pims.dto.ApplyResidentDTO;
import com.pims.dto.ChangePasswordDTO;
import com.pims.dto.LoginDTO;
import com.pims.dto.RegisterDTO;
import com.pims.entity.AuditRecord;
import com.pims.entity.Resident;
import com.pims.vo.LoginVO;

/**
 * 用户Service接口
 */
public interface IUserService {
    
    /**
     * 发送验证码
     */
    void sendVerifyCode(String phone);
    
    /**
     * 用户注册
     */
    void register(RegisterDTO dto);
    
    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);
    
    /**
     * 提交入住申请
     */
    void applyResident(Long userId, ApplyResidentDTO dto);
    
    /**
     * 查询入住申请状态
     */
    AuditRecord getAuditStatus(Long userId);
    
    /**
     * 获取住户信息
     */
    Resident getResidentInfo(Long userId);
    
    /**
     * 修改密码
     */
    void changePassword(Long userId, ChangePasswordDTO dto);
}
