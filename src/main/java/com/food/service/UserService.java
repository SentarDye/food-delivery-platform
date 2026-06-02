package com.food.service;

import com.food.common.Result;
import com.food.entity.User;
import com.food.mapper.UserMapper;
import com.food.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public Result register(String phone, String password, String nickname) {
        // 仅检查未删除的用户
        User exist = userMapper.selectByPhone(phone);
        if (exist != null) {
            return Result.fail(400, "手机号已注册");
        }
        User user = new User();
        user.setPhone(phone);
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setNickname(nickname != null ? nickname : "用户" + phone.substring(7));
        // balance 和 is_deleted 使用数据库默认值
        userMapper.insert(user);
        return Result.success("注册成功");
    }

    public Result login(String phone, String password) {
        User user = userMapper.selectByPhone(phone);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            return Result.fail(401, "手机号或密码错误");
        }
        String token = JwtUtil.generateToken(user.getId(), user.getPhone(), "USER");
        return Result.success(token);
    }

    public Result getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        user.setPassword("密码不可见");
        return Result.success(user);
    }
}
