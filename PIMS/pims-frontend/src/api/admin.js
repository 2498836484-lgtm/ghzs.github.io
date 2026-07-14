import request from './request'

// 管理员登录
export function adminLogin(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

// 获取待审核列表
export function getPendingList(params) {
  return request({
    url: '/admin/audit/pending-list',
    method: 'get',
    params
  })
}

// 处理审核
export function processAudit(adminId, data) {
  return request({
    url: '/admin/audit/process',
    method: 'post',
    params: { adminId },
    data
  })
}

// 查询所有缴费记录
export function getAllPaymentList(params) {
  return request({
    url: '/admin/payment/list',
    method: 'get',
    params
  })
}

// 获取管理员列表
export function getAdminList(params) {
  return request({
    url: '/admin/list',
    method: 'get',
    params
  })
}

// 创建新管理员（仅超级管理员）
export function createAdmin(data) {
  return request({
    url: '/admin/create',
    method: 'post',
    data
  })
}

// 修改管理员密码
export function changeAdminPassword(data) {
  return request({
    url: '/admin/change-password',
    method: 'post',
    data
  })
}

// 获取待审核列表（别名）
export function getPendingAuditList(params) {
  return getPendingList(params)
}

// 查询所有缴费记录（别名）
export function getAllPaymentRecords(params) {
  return getAllPaymentList(params)
}

// 获取住户列表
export function getResidentList(params) {
  return request({
    url: '/resident/list',
    method: 'get',
    params
  })
}

// 更新住户信息
export function updateResident(data) {
  return request({
    url: '/resident/update',
    method: 'post',
    data
  })
}

// 退房
export function checkoutResident(residentId) {
  return request({
    url: '/resident/checkout',
    method: 'post',
    params: { residentId }
  })
}
