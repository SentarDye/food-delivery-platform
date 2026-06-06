// 技术栈：Axios
import request from './request'

export const merchantApi = {
    getList: (lat?: number, lng?: number) =>
        request.get('/user/merchant/list', { params: { lat, lng } }),
    getDetail: (id: number) => request.get(`/user/merchant/${id}`)
}