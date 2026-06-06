// 技术栈：Axios
import request from './request'

export const cartApi = {
    add: (menuItemId: number, quantity: number = 1) =>
        request.post('/user/cart/add', null, { params: { menuItemId, quantity } }),
    list: () => request.get('/user/cart/list'),
    update: (cartId: number, quantity: number) =>
        request.post('/user/cart/update', null, { params: { cartId, quantity } }),
    remove: (cartId: number) =>
        request.post('/user/cart/remove', null, { params: { cartId } }),
    clear: () => request.post('/user/cart/clear')
}