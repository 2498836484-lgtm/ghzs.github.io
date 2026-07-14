package com.pims.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pims.common.Result;
import com.pims.common.ResultCode;
import com.pims.dto.ResidentUpdateDTO;
import com.pims.entity.Resident;
import com.pims.exception.BusinessException;
import com.pims.mapper.ResidentMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 住户管理控制器（管理员使用）
 */
@Slf4j
@RestController
@RequestMapping("/resident")
@Api(tags = "住户管理")
public class ResidentController {

    @Autowired
    private ResidentMapper residentMapper;

    /**
     * 分页查询住户列表
     */
    @GetMapping("/list")
    @ApiOperation("查询住户列表")
    public Result<IPage<Resident>> getResidentList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String roomNumber) {
        
        Page<Resident> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        
        if (roomNumber != null && !roomNumber.isEmpty()) {
            wrapper.like(Resident::getRoomNumber, roomNumber);
        }
        
        wrapper.orderByDesc(Resident::getCreatedTime);
        
        IPage<Resident> residentPage = residentMapper.selectPage(page, wrapper);
        return Result.success(residentPage);
    }

    /**
     * 根据ID查询住户详情
     */
    @GetMapping("/detail/{id}")
    @ApiOperation("查询住户详情")
    public Result<Resident> getResidentDetail(@PathVariable Long id) {
        Resident resident = residentMapper.selectById(id);
        return Result.success(resident);
    }

    /**
     * 根据用户ID查询住户信息
     */
    @GetMapping("/by-user/{userId}")
    @ApiOperation("根据用户ID查询住户信息")
    public Result<Resident> getResidentByUserId(@PathVariable Long userId) {
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resident::getUserId, userId);
        Resident resident = residentMapper.selectOne(wrapper);
        return Result.success(resident);
    }

    /**
     * 更新住户信息
     */
    @PostMapping("/update")
    @ApiOperation("更新住户信息")
    public Result<?> updateResident(@Validated @RequestBody ResidentUpdateDTO dto) {
        // 1. 查询住户是否存在
        Resident resident = residentMapper.selectById(dto.getId());
        if (resident == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "住户信息不存在");
        }

        // 2. 更新允许修改的字段
        if (dto.getPricePerSqm() != null) {
            resident.setPricePerSqm(dto.getPricePerSqm());
        }
        if (dto.getStatus() != null) {
            resident.setStatus(dto.getStatus());
        }

        // 3. 执行更新
        residentMapper.updateById(resident);
        
        log.info("更新住户信息成功：residentId={}", dto.getId());
        return Result.success("更新成功");
    }

    /**
     * 退房
     */
    @PostMapping("/checkout")
    @ApiOperation("退房")
    public Result<?> checkout(@RequestParam Long residentId) {
        // 1. 查询住户是否存在
        Resident resident = residentMapper.selectById(residentId);
        if (resident == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "住户信息不存在");
        }

        // 2. 检查是否已退房
        if (resident.getStatus() == 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "该住户已退房");
        }

        // 3. TODO: 检查物业费是否结清（后续实现）

        // 4. 更新为未入住状态
        resident.setStatus(0);
        residentMapper.updateById(resident);
        
        log.info("退房成功：residentId={}", residentId);
        return Result.success("退房成功");
    }
}
