package com.xiaomai.controller;

import com.xiaomai.dao.User;
import com.xiaomai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> param) {
        Map<String, Object> res = new HashMap<>();
        String username = param.get("username");
        String password = param.get("password");

        User user = userService.login(username, password);
        if (user != null) {
            // 新增日志：打印数据库中该用户的角色
            System.out.println("当前登录用户角色：" + user.getRole());

            res.put("code", 200);
            res.put("msg", "登录成功");
            res.put("userId", user.getId());
            res.put("username", user.getUsername());
            res.put("role", user.getRole());
        } else {
            res.put("code", 401);
            res.put("msg", "账号或密码错误");
        }
        return res;
    }
}