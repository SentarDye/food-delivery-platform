// 技术栈：Spring Boot + MyBatis + BCrypt
package com.food.service;

import com.food.common.Result;
import com.food.entity.Rider;
import com.food.mapper.RiderMapper;
import com.food.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RiderService {
    @Autowired
    private RiderMapper riderMapper;

    public Result register(String phone, String password, String name) {
        Rider exist = riderMapper.selectByPhone(phone);
        if (exist != null) {
            return Result.fail(400, "手机号已注册");
        }
        Rider rider = new Rider();
        rider.setPhone(phone);
        rider.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        rider.setName(name);
        rider.setStatus(0);  // 默认空闲
        riderMapper.insert(rider);
        return Result.success("注册成功");
    }

    public Result login(String phone, String password) {
        Rider rider = riderMapper.selectByPhone(phone);
        if (rider == null || !BCrypt.checkpw(password, rider.getPassword())) {
            return Result.fail(401, "手机号或密码错误");
        }
        String token = JwtUtil.generateToken(rider.getId(), rider.getPhone(), "RIDER");
        return Result.success(token);
    }

    public Result getInfo(Long riderId) {
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null) {
            return Result.fail(404, "骑手不存在");
        }
        rider.setPassword(null);
        return Result.success(rider);
    }

    // 更新骑手状态（在线/忙碌/休息）
    public Result updateStatus(Long riderId, Integer status) {
        if (status < 0 || status > 2) {
            return Result.fail(400, "无效状态");
        }
        Rider rider = riderMapper.selectById(riderId);
        if (rider == null) {
            return Result.fail(404, "骑手不存在");
        }
        rider.setStatus(status);
        riderMapper.updateById(rider);
        return Result.success("状态更新成功");
    }
}
