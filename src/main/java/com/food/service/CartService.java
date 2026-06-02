// 技术栈：Spring Boot + MyBatis
package com.food.service;

import com.food.common.Result;
import com.food.entity.Cart;
import com.food.entity.MenuItem;
import com.food.mapper.CartMapper;
import com.food.mapper.MenuItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private MenuItemMapper menuItemMapper;

    /**
     * 添加商品到购物车，若已存在则增加数量
     */
    @Transactional
    public Result addToCart(Long userId, Long menuItemId, Integer quantity) {
        // 检查菜品是否存在且上架
        MenuItem item = menuItemMapper.selectById(menuItemId);
        if (item == null || item.getStatus() != 1) {
            return Result.fail(404, "菜品不存在或已下架");
        }
        // 检查购物车中是否已有该菜品
        Cart existing = cartMapper.selectByUserIdAndItemId(userId, menuItemId);
        if (existing != null) {
            int newQty = existing.getQuantity() + quantity;
            cartMapper.updateQuantity(existing.getId(), newQty);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setMerchantId(item.getMerchantId());
            cart.setMenuItemId(menuItemId);
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
        }
        return Result.success("添加成功");
    }

    /**
     * 修改购物车中某商品的数量
     */
    public Result updateCartItem(Long userId, Long cartId, Integer quantity) {
        // 简单校验：该购物车记录属于当前用户
        List<Cart> userCart = cartMapper.selectByUserId(userId);
        boolean belongs = userCart.stream().anyMatch(c -> c.getId().equals(cartId));
        if (!belongs) {
            return Result.fail(403, "无权操作");
        }
        if (quantity <= 0) {
            cartMapper.deleteById(cartId);
        } else {
            cartMapper.updateQuantity(cartId, quantity);
        }
        return Result.success("更新成功");
    }

    /**
     * 删除购物车中的商品
     */
    public Result removeCartItem(Long userId, Long cartId) {
        return updateCartItem(userId, cartId, 0);
    }

    /**
     * 清空用户购物车
     */
    public Result clearCart(Long userId) {
        cartMapper.deleteByUserId(userId);
        return Result.success("购物车已清空");
    }

    /**
     * 查看用户购物车（包含菜品详情）
     */
    public Result getCartList(Long userId) {
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        // 可以进一步关联菜品信息，这里简单返回购物车记录
        return Result.success(cartList);
    }
}
