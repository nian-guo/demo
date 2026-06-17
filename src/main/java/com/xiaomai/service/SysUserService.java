package com.xiaomai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomai.dao.SysUser;
import java.util.List;

public interface SysUserService extends IService<SysUser> {
    // 查询全部用户
    List<SysUser> listAllUser();
    // 新增用户（自动填充创建时间、日期、默认启用）
    String addUser(SysUser sysUser);
    // 根据id切换状态
    boolean toggleUserStatus(Long id);
    // 重置密码为123456
    boolean resetPassword(Long id);
    // 根据id查询用户
    SysUser getUserById(Long id);
    // 根据用户名查询（判重）
    SysUser getUserByUsername(String username);
}