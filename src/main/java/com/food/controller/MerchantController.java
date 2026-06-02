package com.food.controller;

import com.food.common.Result;
import com.food.service.MerchantService;
import com.food.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/merchant")
public class MerchantController extends BaseController {
    @Autowired
    private MerchantService merchantService;

    @PostMapping("/register")
    public Result register(@RequestParam String name,
                           @RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam(required = false) String address) {
        return merchantService.register(name, phone, password, address);
    }

    @PostMapping("/login")
    public Result login(@RequestParam String phone,
                        @RequestParam String password) {
        return merchantService.login(phone, password);
    }

    @GetMapping("/info")
    public Result getInfo(HttpServletRequest request) {
        Long merchantId = getMerchantId(request);
        return merchantService.getMerchantInfo(merchantId);
    }
}
