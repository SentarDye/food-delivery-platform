// 技术栈：Spring Boot + JWT (jjwt)
package com.food.controller;

import com.food.util.JwtUtil;
import io.jsonwebtoken.Claims;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 控制器基类，运用模板方法模式封装 JWT 解析，子类无需重复编写
 */
public abstract class BaseController {

    /**
     * 从请求头中提取 JWT Claims
     */
    protected Claims getClaims(HttpServletRequest request) {
        String token = request.getHeader("token");
        return JwtUtil.parseToken(token);
    }

    /**
     * 获取当前登录用户 ID
     */
    protected Long getUserId(HttpServletRequest request) {
        return getClaims(request).get("userId", Long.class);
    }

    /**
     * 获取当前登录商家 ID
     */
    protected Long getMerchantId(HttpServletRequest request) {
        return getClaims(request).get("userId", Long.class);
    }

    /**
     * 获取当前登录骑手 ID
     */
    protected Long getRiderId(HttpServletRequest request) {
        return getClaims(request).get("userId", Long.class);
    }

    /**
     * 获取当前登录用户角色
     */
    protected String getRole(HttpServletRequest request) {
        return getClaims(request).get("role", String.class);
    }
}
