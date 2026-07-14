import request from './request'

// 发送验证码
export function sendVerifyCode(phone) {
  return request({
    url: '/user/send-code',
    method: 'post',
    params: { phone }
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

// 用户登录（房号）
export function loginByRoom(data) {
  return request({
    url: '/user/login/room',
    method: 'post',
    data
  })
}

// 用户登录（手机号)
export function loginByPhone(data) {
  return request({
    url: '/user/login/phone',
    method: 'post',
    data
  })
}

// 提交入住申请
export function applyResident(userId, data) {
  return request({
    url: '/user/apply-resident',
    method: 'post',
    params: { userId },
    data
  })
}

// 查询入住申请状态
export function getAuditStatus(userId) {
  return request({
    url: '/user/audit-status',
    method: 'get',
    params: { userId }
  })
}

// 获取住户信息
export function getResidentInfo(userId) {
  return request({
    url: '/user/resident-info',
    method: 'get',
    params: { userId }
  })
}

// 修改用户密码
export function changePassword(userId, data) {
  return request({
    url: '/user/change-password',
    method: 'post',
    params: { userId },
    data
  })
}
