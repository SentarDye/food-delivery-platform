// 技术栈：Spring Boot + JWT
package com.food.controller;

import com.food.common.Result;
import com.food.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user/address")
public class AddressController extends BaseController {

    @Autowired
    private AddressService addressService;

    @PostMapping("/add")
    public Result add(@RequestParam String receiverName,
                      @RequestParam String receiverPhone,
                      @RequestParam(required = false) String province,
                      @RequestParam(required = false) String city,
                      @RequestParam(required = false) String district,
                      @RequestParam String detail,
                      @RequestParam(required = false) BigDecimal latitude,
                      @RequestParam(required = false) BigDecimal longitude,
                      @RequestParam(required = false, defaultValue = "0") Integer isDefault,
                      HttpServletRequest request) {
        Long userId = getUserId(request);
        return addressService.addAddress(userId, receiverName, receiverPhone,
                province, city, district, detail, latitude, longitude, isDefault);
    }

    @PostMapping("/update")
    public Result update(@RequestParam Long addressId,
                         @RequestParam String receiverName,
                         @RequestParam String receiverPhone,
                         @RequestParam(required = false) String province,
                         @RequestParam(required = false) String city,
                         @RequestParam(required = false) String district,
                         @RequestParam String detail,
                         @RequestParam(required = false) BigDecimal latitude,
                         @RequestParam(required = false) BigDecimal longitude,
                         @RequestParam(required = false, defaultValue = "0") Integer isDefault,
                         HttpServletRequest request) {
        Long userId = getUserId(request);
        return addressService.updateAddress(userId, addressId, receiverName, receiverPhone,
                province, city, district, detail, latitude, longitude, isDefault);
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Long addressId,
                         HttpServletRequest request) {
        Long userId = getUserId(request);
        return addressService.deleteAddress(userId, addressId);
    }

    @GetMapping("/list")
    public Result list(HttpServletRequest request) {
        Long userId = getUserId(request);
        return addressService.listAddresses(userId);
    }

    @GetMapping("/default")
    public Result getDefault(HttpServletRequest request) {
        Long userId = getUserId(request);
        return addressService.getDefaultAddress(userId);
    }
}
