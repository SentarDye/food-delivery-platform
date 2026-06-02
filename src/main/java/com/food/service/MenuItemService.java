package com.food.service;

import com.food.common.Result;
import com.food.entity.MenuItem;
import com.food.mapper.MenuItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class MenuItemService {
    @Autowired
    private MenuItemMapper menuItemMapper;

    // 添加菜品，支持分类ID（可选）
    public Result addMenuItem(Long merchantId, String name, BigDecimal price,
                              String image, String description, Long categoryId) {
        MenuItem item = new MenuItem();
        item.setMerchantId(merchantId);
        item.setName(name);
        item.setPrice(price);
        item.setImage(image);
        item.setDescription(description);
        item.setCategoryId(categoryId);
        item.setStatus(1);      // 默认上架
        item.setStock(-1);      // 无限库存
        item.setSortOrder(0);
        item.setSellCount(0);
        item.setMaxPurchasePerUser(0);
        menuItemMapper.insert(item);
        return Result.success(item);
    }

    public Result updateMenuItem(Long merchantId, Long itemId, String name, BigDecimal price,
                                 String image, String description, Integer status,
                                 Long categoryId, Integer stock, Integer sortOrder,
                                 Integer maxPurchasePerUser) {
        MenuItem item = menuItemMapper.selectById(itemId);
        if (item == null || !item.getMerchantId().equals(merchantId)) {
            return Result.fail(403, "无权操作该菜品");
        }
        item.setName(name);
        item.setPrice(price);
        item.setImage(image);
        item.setDescription(description);
        item.setStatus(status);
        item.setCategoryId(categoryId);
        item.setStock(stock);
        item.setSortOrder(sortOrder);
        item.setMaxPurchasePerUser(maxPurchasePerUser);
        menuItemMapper.updateById(item);
        return Result.success("修改成功");
    }

    public Result deleteMenuItem(Long merchantId, Long itemId) {
        MenuItem item = menuItemMapper.selectById(itemId);
        if (item == null || !item.getMerchantId().equals(merchantId)) {
            return Result.fail(403, "无权操作");
        }
        // 软删除
        menuItemMapper.softDeleteById(itemId);
        return Result.success("删除成功");
    }

    public Result getMenuItemList(Long merchantId) {
        List<MenuItem> list = menuItemMapper.selectByMerchantId(merchantId);
        return Result.success(list);
    }
}
