// 技术栈：Axios
import request from './request'

export const addressApi = {
    list: () => request.get('/user/address/list'),
    add: (data: any) => request.post('/user/address/add', null, { params: data }),
    update: (data: any) => request.post('/user/address/update', null, { params: data }),
    remove: (addressId: number) =>
        request.post('/user/address/delete', null, { params: { addressId } }),
    getDefault: () => request.get('/user/address/default')
}