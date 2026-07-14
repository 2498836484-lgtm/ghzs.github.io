import request from './request'

// 提交缴费记录
export function submitPayment(data) {
  return request({
    url: '/payment/submit',
    method: 'post',
    data
  })
}

// 查询缴费记录列表
export function getPaymentList(params) {
  return request({
    url: '/payment/list',
    method: 'get',
    params
  })
}

// 查询缴费记录详情
export function getPaymentDetail(id) {
  return request({
    url: `/payment/detail/${id}`,
    method: 'get'
  })
}

// 查询缴费记录（别名）
export function getPaymentRecords(params) {
  return getPaymentList(params)
}
