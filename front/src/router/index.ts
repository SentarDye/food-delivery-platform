// 技术栈：Vue Router 4, TypeScript
import { createRouter, createWebHashHistory, type RouteRecordRaw } from 'vue-router'
import { getToken } from '../utils/auth'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/user/Login.vue'),
        meta: { title: '登录', noAuth: true }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/user/Register.vue'),
        meta: { title: '注册', noAuth: true }
    },
    {
        path: '/home',
        name: 'Home',
        component: () => import('../views/user/Home.vue'),
        meta: { title: '首页' }
    },
    {
        path: '/merchant/:id',
        name: 'MerchantDetail',
        component: () => import('../views/user/MerchantDetail.vue'),
        meta: { title: '商家详情' }
    },
    {
        path: '/cart',
        name: 'Cart',
        component: () => import('../views/user/Cart.vue'),
        meta: { title: '购物车' }
    },
    {
        path: '/checkout',
        name: 'Checkout',
        component: () => import('../views/user/Checkout.vue'),
        meta: { title: '确认订单' }
    },
    {
        path: '/orders',
        name: 'OrderList',
        component: () => import('../views/user/OrderList.vue'),
        meta: { title: '我的订单' }
    },
    {
        path: '/order/:id',
        name: 'OrderDetail',
        component: () => import('../views/user/OrderDetail.vue'),
        meta: { title: '订单详情' }
    },
    {
        path: '/address',
        name: 'Address',
        component: () => import('../views/user/AddressManage.vue'),
        meta: { title: '地址管理' }
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('../views/user/Profile.vue'),
        meta: { title: '个人中心' }
    },
    {
        path: '/',
        redirect: '/home'
    },
    // 商户端路由
    {
        path: '/merchant',
        redirect: '/merchant/login'
    },
    {
        path: '/merchant/login',
        name: 'MerchantLogin',
        component: () => import('../views/merchant/Login.vue'),
        meta: { title: '商家登录', noAuth: true }
    },
    {
        path: '/merchant/register',
        name: 'MerchantRegister',
        component: () => import('../views/merchant/Register.vue'),
        meta: { title: '商家注册', noAuth: true }
    },
    {
        path: '/merchant/dashboard',
        name: 'MerchantDashboard',
        component: () => import('../views/merchant/Dashboard.vue'),
        meta: { title: '商家中心' }
    },
    {
        path: '/merchant/menu',
        name: 'MerchantMenuManage',
        component: () => import('../views/merchant/MenuManage.vue'),
        meta: { title: '菜品管理' }
    },
    {
        path: '/merchant/orders',
        name: 'MerchantOrderManage',
        component: () => import('../views/merchant/OrderManage.vue'),
        meta: { title: '订单管理' }
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

// 路由守卫：需登录的页面检查token
router.beforeEach((to, from, next) => {
    if (to.meta.noAuth) return next()
    if (to.path.startsWith('/merchant')) {
        const token = getToken('merchant_token')
        if (!token) {
            next('/merchant/login')
        } else {
            next()
        }
    } else {
        const token = getToken()
        if (!token) {
            next('/login')
        } else {
            next()
        }
    }
})

export default router