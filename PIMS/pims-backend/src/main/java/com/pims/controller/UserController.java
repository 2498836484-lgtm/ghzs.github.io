package com.pims.controller;

import com.pims.common.Result;
import com.pims.dto.ApplyResidentDTO;
import com.pims.dto.ChangePasswordDTO;
import com.pims.dto.LoginDTO;
import com.pims.dto.RegisterDTO;
import com.pims.entity.AuditRecord;
import com.pims.entity.Resident;
import com.pims.service.IUserService;
import com.pims.vo.LoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Controller
 */
@Slf4j
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @ApiOperation("发送验证码")
    @PostMapping("/send-code")
    public Result<?> sendCode(@RequestParam String phone) {
        userService.sendVerifyCode(phone);
        return Result.success("验证码已发送，请查看控制台日志");
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<?> register(@Validated @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success("注册成功");
    }

    @ApiOperation("用户登录（房号登录）")
    @PostMapping("/login/room")
    public Result<LoginVO> loginByRoom(@Validated @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    @ApiOperation("用户登录（手机号登录）")
    @PostMapping("/login/phone")
    public Result<LoginVO> loginByPhone(@Validated @RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    @ApiOperation("提交入住申请")
    @PostMapping("/apply-resident")
    public Result<?> applyResident(@RequestParam Long userId, 
                                    @Validated @RequestBody ApplyResidentDTO dto) {
        userService.applyResident(userId, dto);
        return Result.success("申请提交成功，请等待审核");
    }

    @ApiOperation("查询入住申请状态")
    @GetMapping("/audit-status")
    public Result<AuditRecord> getAuditStatus(@RequestParam Long userId) {
        AuditRecord record = userService.getAuditStatus(userId);
        return Result.success(record);
    }

    @ApiOperation("获取住户信息")
    @GetMapping("/resident-info")
    public Result<Resident> getResidentInfo(@RequestParam Long userId) {
        Resident resident = userService.getResidentInfo(userId);
        return Result.success(resident);
    }

    @ApiOperation("修改密码")
    @PostMapping("/change-password")
    public Result<?> changePassword(@RequestParam Long userId,
                                     @Validated @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(userId, dto);
        return Result.success("密码修改成功");
    }
}
