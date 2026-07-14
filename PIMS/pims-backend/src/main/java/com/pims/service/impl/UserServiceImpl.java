package com.pims.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pims.common.Constants;
import com.pims.common.ResultCode;
import com.pims.dto.ApplyResidentDTO;
import com.pims.dto.ChangePasswordDTO;
import com.pims.dto.LoginDTO;
import com.pims.dto.RegisterDTO;
import com.pims.entity.AuditRecord;
import com.pims.entity.Resident;
import com.pims.entity.User;
import com.pims.exception.BusinessException;
import com.pims.mapper.AuditRecordMapper;
import com.pims.mapper.ResidentMapper;
import com.pims.mapper.UserMapper;
import com.pims.service.IUserService;
import com.pims.utils.*;
import com.pims.vo.LoginVO;
import com.pims.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户Service实现
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ResidentMapper residentMapper;

    @Autowired
    private AuditRecordMapper auditRecordMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerifyCodeUtil verifyCodeUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void sendVerifyCode(String phone) {
        // 验证手机号格式
        if (!PhoneValidator.isValid(phone)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "手机号格式不正确");
        }

        // 生成验证码
        String code = verifyCodeUtil.generateCode();

        // 发送验证码（模拟，打印到日志）
        verifyCodeUtil.sendCode(phone, code);

        log.info("验证码已发送：手机号={}, 验证码={}", phone, code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        // 1. 验证手机号格式
        if (!PhoneValidator.isValid(dto.getPhone())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "手机号格式不正确");
        }

        // 2. 验证房号格式
        if (!RoomValidator.isValid(dto.getRoomNumber())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "房号格式不正确（格式：Xxxx，如A310）");
        }

        // 3. 检查手机号是否已注册
        User existPhone = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, dto.getPhone()));
        if (existPhone != null) {
            throw new BusinessException(ResultCode.PHONE_EXISTS);
        }

        // 4. 检查房号是否已存在
        User existRoom = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getRoomNumber, dto.getRoomNumber()));
        if (existRoom != null) {
            throw new BusinessException(ResultCode.ROOM_NUMBER_EXISTS);
        }

        // 5. 验证验证码
        if (!verifyCodeUtil.verify(dto.getPhone(), dto.getVerifyCode())) {
            throw new BusinessException(ResultCode.VERIFY_CODE_ERROR);
        }

        // 6. 创建用户
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoomNumber(dto.getRoomNumber());
        user.setStatus(0);  // 未入住

        userMapper.insert(user);

        log.info("用户注册成功：手机号={}, 房号={}", dto.getPhone(), dto.getRoomNumber());
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 判断是手机号还是房号登录
        User user;
        if (PhoneValidator.isValid(dto.getAccount())) {
            // 手机号登录
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, dto.getAccount()));
        } else if (RoomValidator.isValid(dto.getAccount())) {
            // 房号登录
            user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getRoomNumber, dto.getAccount()));
        } else {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "账号格式不正确");
        }

        // 验证用户是否存在
        if (user == null) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 检查用户状态
        if (user.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.USER_DELETED);
        }

        // 验证密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), "user");

        // 组装返回数据
        LoginVO vo = new LoginVO();
        vo.setToken(token);

        UserInfoVO userInfo = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfo);
        vo.setUserInfo(userInfo);

        log.info("用户登录成功：userId={}", user.getId());

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyResident(Long userId, ApplyResidentDTO dto) {
        // 1. 验证身份证号格式
        if (!IdCardValidator.isValid(dto.getIdCard())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "身份证号格式不正确");
        }

        // 2. 验证面积
        if (dto.getArea().doubleValue() <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "住房面积必须大于0");
        }

        // 3. 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 4. 检查是否已有待审核的申请
        AuditRecord existRecord = auditRecordMapper.selectOne(new LambdaQueryWrapper<AuditRecord>()
                .eq(AuditRecord::getUserId, userId)
                .eq(AuditRecord::getStatus, 0));
        if (existRecord != null) {
            throw new BusinessException(ResultCode.AUDIT_PENDING);
        }

        // 5. 创建审核记录
        AuditRecord auditRecord = new AuditRecord();
        auditRecord.setUserId(userId);
        auditRecord.setName(dto.getName());
        auditRecord.setIdCard(dto.getIdCard());
        auditRecord.setArea(dto.getArea());
        auditRecord.setRoomNumber(user.getRoomNumber());
        auditRecord.setStatus(0);  // 待审核

        auditRecordMapper.insert(auditRecord);

        log.info("用户提交入住申请：userId={}", userId);
    }

    @Override
    public AuditRecord getAuditStatus(Long userId) {
        // 查询最新的审核记录
        return auditRecordMapper.selectOne(new LambdaQueryWrapper<AuditRecord>()
                .eq(AuditRecord::getUserId, userId)
                .orderByDesc(AuditRecord::getCreatedTime)
                .last("LIMIT 1"));
    }

    @Override
    public Resident getResidentInfo(Long userId) {
        return residentMapper.selectOne(new LambdaQueryWrapper<Resident>()
                .eq(Resident::getUserId, userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordDTO dto) {
        // 1. 验证手机号格式
        if (!PhoneValidator.isValid(dto.getPhone())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "手机号格式不正确");
        }

        // 2. 验证验证码
        if (!verifyCodeUtil.verify(dto.getPhone(), dto.getVerifyCode())) {
            throw new BusinessException(ResultCode.VERIFY_CODE_ERROR);
        }

        // 3. 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 4. 验证手机号必须和当前登录用户的手机号一致
        if (!user.getPhone().equals(dto.getPhone())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "手机号输入错误");
        }

        // 5. 验证原密码必须和注册时设置的密码一致
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "原密码错误");
        }

        // 6. 更新密码
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);

        log.info("用户修改密码成功：userId={}", userId);
    }
}
