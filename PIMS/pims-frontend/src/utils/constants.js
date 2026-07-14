// 常量定义

// 用户状态
export const USER_STATUS = {
  NOT_RESIDENT: 0, // 未入住
  RESIDENT: 1      // 已入住
}

// 审核状态
export const AUDIT_STATUS = {
  PENDING: 0,   // 待审核
  APPROVED: 1,  // 审核通过
  REJECTED: 2   // 审核拒绝
}

// 消息类型
export const MESSAGE_TYPE = {
  PAYMENT_REMINDER: 1 // 缴费提醒
}

// 消息状态
export const MESSAGE_STATUS = {
  UNREAD: 0, // 未读
  READ: 1    // 已读
}

// 用户类型
export const USER_TYPE = {
  USER: 'user',
  ADMIN: 'admin'
}
