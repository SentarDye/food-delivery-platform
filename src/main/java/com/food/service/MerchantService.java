package com.food.service;

import com.food.common.Result;
import com.food.entity.Merchant;
import com.food.mapper.MerchantMapper;
import com.food.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MerchantService {
    @Autowired
    private MerchantMapper merchantMapper;

    public Result register(String name, String phone, String password, String address) {
        Merchant exist = merchantMapper.selectByPhone(phone);
        if (exist != null) {
            return Result.fail(400, "手机号已注册");
        }
        Merchant merchant = new Merchant();
        merchant.setName(name);
        merchant.setPhone(phone);
        merchant.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        merchant.setAddress(address);
        merchant.setStatus(0);  // 待审核
        // 其他字段使用数据库默认值
        merchantMapper.insert(merchant);
        return Result.success("注册成功，请等待审核");
    }

    public Result login(String phone, String password) {
        Merchant merchant = merchantMapper.selectByPhone(phone);
        if (merchant == null || !BCrypt.checkpw(password, merchant.getPassword())) {
            return Result.fail(401, "手机号或密码错误");
        }
        if (merchant.getStatus() != 1) {
            return Result.fail(403, "商家未通过审核");
        }
        String token = JwtUtil.generateToken(merchant.getId(), merchant.getPhone(), "MERCHANT");
        return Result.success(token);
    }

    public Result getMerchantInfo(Long merchantId) {
        Merchant merchant = merchantMapper.selectById(merchantId);
        if (merchant == null) {
            return Result.fail(404, "商家不存在");
        }
        merchant.setPassword(null);
        return Result.success(merchant);
    }
}
