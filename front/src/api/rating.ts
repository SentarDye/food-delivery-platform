// 技术栈：Axios
import request from './request'

export const ratingApi = {
  submit: (orderId: number, score: number, content?: string, images?: string) =>
    request.post('/user/rating/submit', null, { params: { orderId, score, content, images } }),
  getMerchantRatings: (merchantId: number) =>
    request.get('/merchant/rating/list', { params: { merchantId } })
}