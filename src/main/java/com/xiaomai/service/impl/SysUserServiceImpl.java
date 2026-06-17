package com.xiaomai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomai.dao.SysUser;
import com.xiaomai.dao.mapper.SysUserMapper;
import com.xiaomai.service.SysUserService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    @Override
    public List<SysUser> listAllUser() {
        return list();
    }

    @Override
    public String addUser(SysUser sysUser) {
        // 用户名查重
        SysUser exist = getUserByUsername(sysUser.getUsername());
        if (exist != null) {
            return "用户名已存在";
        }
        // 默认启用
        sysUser.setStatus("active");
        // 自动填充创建日期、创建时间、更新时间
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        sysUser.setCreatedAt(today);
        sysUser.setCreateTime(now);
        sysUser.setUpdateTime(now);
        boolean save = save(sysUser);
        return save ? "success" : "新增失败";
    }

    @Override
    public boolean toggleUserStatus(Long id) {
        SysUser user = getById(id);
        if (user == null) return false;
        // 状态互转
        String newStatus = "active".equals(user.getStatus()) ? "disabled" : "active";
        user.setStatus(newStatus);
        user.setUpdateTime(LocalDateTime.now());
        return updateById(user);
    }

    @Override
    public boolean resetPassword(Long id) {
        SysUser updateUser = new SysUser();
        updateUser.setId(id);
        updateUser.setPassword("123456");
        updateUser.setUpdateTime(LocalDateTime.now());
        return updateById(updateUser);
    }

    @Override
    public SysUser getUserById(Long id) {
        return getById(id);
    }

    @Override
    public SysUser getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return getOne(wrapper);
    }
}