// 技术栈：Spring Boot + JWT
package com.food.controller;

import com.food.common.Result;
import com.food.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user/cart")
public class CartController extends BaseController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result add(@RequestParam Long menuItemId,
                      @RequestParam(defaultValue = "1") Integer quantity,
                      HttpServletRequest request) {
        Long userId = getUserId(request);
        return cartService.addToCart(userId, menuItemId, quantity);
    }

    @PostMapping("/update")
    public Result update(@RequestParam Long cartId,
                         @RequestParam Integer quantity,
                         HttpServletRequest request) {
        Long userId = getUserId(request);
        return cartService.updateCartItem(userId, cartId, quantity);
    }

    @PostMapping("/remove")
    public Result remove(@RequestParam Long cartId,
                         HttpServletRequest request) {
        Long userId = getUserId(request);
        return cartService.removeCartItem(userId, cartId);
    }

    @PostMapping("/clear")
    public Result clear(HttpServletRequest request) {
        Long userId = getUserId(request);
        return cartService.clearCart(userId);
    }

    @GetMapping("/list")
    public Result list(HttpServletRequest request) {
        Long userId = getUserId(request);
        return cartService.getCartList(userId);
    }
}
