package com.xiaomai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomai.dao.User;

public interface UserService extends IService<User> {
    User login(String username, String password);
}