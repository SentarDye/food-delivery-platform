// 技术栈：Axios
import request from './request'

export const couponApi = {
    // 获取用户可用优惠券
    getAvailable: () => request.get('/user/coupon/available')
}