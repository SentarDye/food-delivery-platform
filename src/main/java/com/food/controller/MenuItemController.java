package com.food.controller;

import com.food.common.Result;
import com.food.service.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/merchant/menu")
public class MenuItemController extends BaseController {
    @Autowired
    private MenuItemService menuItemService;

    @PostMapping("/add")
    public Result add(@RequestParam String name,
                      @RequestParam BigDecimal price,
                      @RequestParam(required = false) String image,
                      @RequestParam(required = false) String description,
                      @RequestParam(required = false) Long categoryId,
                      HttpServletRequest request) {
        Long merchantId = getMerchantId(request);
        return menuItemService.addMenuItem(merchantId, name, price, image, description, categoryId);
    }

    @PostMapping("/update")
    public Result update(@RequestParam Long itemId,
                         @RequestParam String name,
                         @RequestParam BigDecimal price,
                         @RequestParam(required = false) String image,
                         @RequestParam(required = false) String description,
                         @RequestParam Integer status,
                         @RequestParam(required = false) Long categoryId,
                         @RequestParam(required = false, defaultValue = "-1") Integer stock,
                         @RequestParam(required = false, defaultValue = "0") Integer sortOrder,
                         @RequestParam(required = false, defaultValue = "0") Integer maxPurchasePerUser,
                         HttpServletRequest request) {
        Long merchantId = getMerchantId(request);
        return menuItemService.updateMenuItem(merchantId, itemId, name, price, image, description,
                status, categoryId, stock, sortOrder, maxPurchasePerUser);
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Long itemId,
                         HttpServletRequest request) {
        Long merchantId = getMerchantId(request);
        return menuItemService.deleteMenuItem(merchantId, itemId);
    }

    @GetMapping("/list")
    public Result list(HttpServletRequest request) {
        Long merchantId = getMerchantId(request);
        return menuItemService.getMenuItemList(merchantId);
    }
}
