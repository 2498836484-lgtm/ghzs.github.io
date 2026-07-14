package com.pims.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pims.common.Result;
import com.pims.dto.PaymentQueryDTO;
import com.pims.service.IMessageService;
import com.pims.utils.JwtUtil;
import com.pims.vo.MessageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 消息管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/message")
@Api(tags = "消息管理")
public class MessageController {

    @Autowired
    private IMessageService messageService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    @ApiOperation("获取未读消息数量")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        Integer count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 获取消息列表
     */
    @GetMapping("/list")
    @ApiOperation("获取消息列表")
    public Result<IPage<MessageVO>> getMessageList(PaymentQueryDTO queryDTO, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        IPage<MessageVO> page = messageService.getMessageList(queryDTO, userId);
        return Result.success(page);
    }

    /**
     * 标记消息为已读
     */
    @PostMapping("/read/{id}")
    @ApiOperation("标记消息为已读")
    public Result<Void> markAsRead(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        messageService.markAsRead(id, userId);
        return Result.success();
    }

    /**
     * 标记所有消息为已读
     */
    @PostMapping("/read/all")
    @ApiOperation("标记所有消息为已读")
    public Result<Void> markAllAsRead(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long userId = jwtUtil.getUserIdFromToken(token);
        
        messageService.markAllAsRead(userId);
        return Result.success();
    }
}
