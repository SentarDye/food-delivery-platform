// 技术栈：Axios
import request from './request'

export const menuApi = {
    getMenuItems: (merchantId: number) =>
        request.get('/user/merchant/menu', { params: { merchantId } })
}