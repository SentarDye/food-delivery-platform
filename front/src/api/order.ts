// 技术栈：Axios
import request from './request'

export const orderApi = {
    place: (addressId: number, userCouponId?: number, remark?: string) =>
        request.post('/order/place', null, { params: { addressId, userCouponId, remark } }),
    list: () => request.get('/order/user/list'),
    detail: (orderId: number) => request.get('/order/detail', { params: { orderId } }),
    pay: (orderId: number) => request.post('/order/pay', null, { params: { orderId } })
}