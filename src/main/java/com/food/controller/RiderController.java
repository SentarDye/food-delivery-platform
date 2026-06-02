// 技术栈：Spring Boot + JWT
package com.food.controller;

import com.food.common.Result;
import com.food.service.RiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/rider")
public class RiderController extends BaseController {

    @Autowired
    private RiderService riderService;

    @PostMapping("/register")
    public Result register(@RequestParam String phone,
                           @RequestParam String password,
                           @RequestParam String name) {
        return riderService.register(phone, password, name);
    }

    @PostMapping("/login")
    public Result login(@RequestParam String phone,
                        @RequestParam String password) {
        return riderService.login(phone, password);
    }

    @GetMapping("/info")
    public Result info(HttpServletRequest request) {
        Long riderId = getRiderId(request);
        return riderService.getInfo(riderId);
    }

    @PostMapping("/status")
    public Result updateStatus(@RequestParam Integer status,
                               HttpServletRequest request) {
        Long riderId = getRiderId(request);
        return riderService.updateStatus(riderId, status);
    }
}
