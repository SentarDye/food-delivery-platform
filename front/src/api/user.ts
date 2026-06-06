// 技术栈：Axios
import request from './request'

export const userApi = {
    register: (phone: string, password: string, nickname?: string) =>
        request.post('/user/register', null, { params: { phone, password, nickname } }),
    login: (phone: string, password: string) =>
        request.post('/user/login', null, { params: { phone, password } }),
    getInfo: () => request.get('/user/info')
}