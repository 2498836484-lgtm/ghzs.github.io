import request from './request'

// 获取未读消息数量
export function getUnreadCount() {
  return request({
    url: '/message/unread/count',
    method: 'get'
  })
}

// 获取消息列表
export function getMessageList(params) {
  return request({
    url: '/message/list',
    method: 'get',
    params
  })
}

// 标记消息为已读
export function markAsRead(id) {
  return request({
    url: `/message/read/${id}`,
    method: 'post'
  })
}

// 标记所有消息为已读
export function markAllAsRead() {
  return request({
    url: '/message/read/all',
    method: 'post'
  })
}
