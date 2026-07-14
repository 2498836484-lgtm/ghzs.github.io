import request from './request'

// 查询住户列表
export function getResidentList(params) {
  return request({
    url: '/resident/list',
    method: 'get',
    params
  })
}

// 查询住户详情
export function getResidentDetail(id) {
  return request({
    url: `/resident/detail/${id}`,
    method: 'get'
  })
}

// 根据用户ID查询住户信息
export function getResidentByUserId(userId) {
  return request({
    url: `/resident/by-user/${userId}`,
    method: 'get'
  })
}
