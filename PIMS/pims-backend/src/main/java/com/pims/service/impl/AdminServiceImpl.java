package com.pims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pims.common.Constants;
import com.pims.common.ResultCode;
import com.pims.dto.AuditDTO;
import com.pims.dto.LoginDTO;
import com.pims.entity.Admin;
import com.pims.entity.AuditRecord;
import com.pims.entity.Resident;
import com.pims.entity.User;
import com.pims.exception.BusinessException;
import com.pims.mapper.AdminMapper;
import com.pims.mapper.AuditRecordMapper;
import com.pims.mapper.ResidentMapper;
import com.pims.mapper.UserMapper;
import com.pims.service.IAdminService;
import com.pims.utils.JwtUtil;
import com.pims.vo.LoginVO;
import com.pims.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 管理员Service实现
 */
@Slf4j
@Service
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AuditRecordMapper auditRecordMapper;

    @Autowired
    private ResidentMapper residentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginVO login(LoginDTO dto) {
        // 查询管理员
        Admin admin = adminMapper.selectOne(new LambdaQueryWrapper<Admin>()
                .eq(Admin::getUsername, dto.getAccount()));

        if (admin == null) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 生成Token
        String token = jwtUtil.generateToken(admin.getId(), "admin");

        // 组装返回数据
        LoginVO vo = new LoginVO();
        vo.setToken(token);

        UserInfoVO adminInfo = new UserInfoVO();
        adminInfo.setId(admin.getId());
        vo.setUserInfo(adminInfo);

        log.info("管理员登录成功：adminId={}", admin.getId());

        return vo;
    }

    @Override
    public Page<AuditRecord> getPendingList(Integer pageNum, Integer pageSize) {
        Page<AuditRecord> page = new Page<>(pageNum, pageSize);
        return auditRecordMapper.selectPage(page, new LambdaQueryWrapper<AuditRecord>()
                .eq(AuditRecord::getStatus, 0)
                .orderByAsc(AuditRecord::getCreatedTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processAudit(AuditDTO dto, Long adminId) {
        // 1. 查询审核记录
        AuditRecord auditRecord = auditRecordMapper.selectById(dto.getAuditId());
        if (auditRecord == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "审核记录不存在");
        }

        // 2. 更新审核记录
        auditRecord.setStatus(dto.getStatus());
        auditRecord.setAdminId(adminId);
        auditRecord.setAuditTime(new Date());
        auditRecordMapper.updateById(auditRecord);

        // 3. 如果审核通过，更新或创建住户信息
        if (dto.getStatus() == 1) {
            // 查询用户信息
            User user = userMapper.selectById(auditRecord.getUserId());
            if (user == null) {
                throw new BusinessException(ResultCode.USER_NOT_FOUND);
            }

            // 先查询是否已存在该房号的住户记录
            Resident existingResident = residentMapper.selectOne(new LambdaQueryWrapper<Resident>()
                    .eq(Resident::getRoomNumber, auditRecord.getRoomNumber()));

            if (existingResident != null) {
                // 如果已存在记录，更新该记录
                existingResident.setUserId(auditRecord.getUserId());
                existingResident.setName(auditRecord.getName());
                existingResident.setIdCard(auditRecord.getIdCard());
                existingResident.setPhone(user.getPhone());
                existingResident.setArea(auditRecord.getArea());
                existingResident.setPricePerSqm(dto.getPricePerSqm() != null ? 
                        dto.getPricePerSqm() : new BigDecimal(Constants.DEFAULT_PRICE_PER_SQM));
                existingResident.setStatus(1);  // 已入住
                existingResident.setRegisterTime(new Date());
                residentMapper.updateById(existingResident);
                
                log.info("审核通过，更新住户信息：residentId={}, roomNumber={}", 
                        existingResident.getId(), auditRecord.getRoomNumber());
            } else {
                // 如果不存在记录，创建新记录
                Resident resident = new Resident();
                resident.setUserId(auditRecord.getUserId());
                resident.setName(auditRecord.getName());
                resident.setIdCard(auditRecord.getIdCard());
                resident.setPhone(user.getPhone());
                resident.setRoomNumber(auditRecord.getRoomNumber());
                resident.setArea(auditRecord.getArea());
                resident.setPricePerSqm(dto.getPricePerSqm() != null ? 
                        dto.getPricePerSqm() : new BigDecimal(Constants.DEFAULT_PRICE_PER_SQM));
                resident.setStatus(1);  // 已入住
                resident.setRegisterTime(new Date());
                residentMapper.insert(resident);
                
                log.info("审核通过，创建住户信息：userId={}, roomNumber={}", 
                        auditRecord.getUserId(), auditRecord.getRoomNumber());
            }

            // 更新用户状态
            user.setStatus(1);
            userMapper.updateById(user);
        }

        log.info("审核完成：auditId={}, status={}", dto.getAuditId(), dto.getStatus());
    }
}
