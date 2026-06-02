package com.food.controller;

import com.food.common.Result;
import com.food.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam(required = false) String nickname) {
        return userService.register(phone, password, nickname);
    }

    @PostMapping("/login")
    public Result login(@RequestParam String phone,
                        @RequestParam String password) {
        return userService.login(phone, password);
    }

    @GetMapping("/info")
    public Result getUserInfo(HttpServletRequest request) {
        Long userId = getUserId(request);
        return userService.getUserInfo(userId);
    }
}
