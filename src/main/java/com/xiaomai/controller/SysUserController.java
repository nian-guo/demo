package com.xiaomai.controller;

import com.xiaomai.config.Result;
import com.xiaomai.dao.SysUser;
import com.xiaomai.service.SysUserService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    /** 用户列表 GET /api/user/list */
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        List<SysUser> list = sysUserService.listAllUser();
        return Result.success(list);
    }

    /** 新增用户 POST /api/user/add */
    @PostMapping("/add")
    public Result<String> add(@RequestBody SysUser sysUser) {
        String res = sysUserService.addUser(sysUser);
        if ("用户名已存在".equals(res)) {
            return Result.fail(res);
        } else if ("success".equals(res)) {
            return Result.success("新增用户成功");
        } else {
            return Result.fail("新增用户失败");
        }
    }

    /** 用户详情 GET /api/user/{id} */
    @GetMapping("/{id}")
    public Result<SysUser> getInfo(@PathVariable Long id) {
        SysUser user = sysUserService.getUserById(id);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        return Result.success(user);
    }

    /** 切换状态 PUT /api/user/status/{id} */
    @PutMapping("/status/{id}")
    public Result<String> toggleStatus(@PathVariable Long id) {
        boolean flag = sysUserService.toggleUserStatus(id);
        return flag ? Result.success("状态修改成功") : Result.fail("操作失败");
    }

    /** 重置密码 PUT /api/user/resetPwd/{id} */
    @PutMapping("/resetPwd/{id}")
    public Result<String> resetPwd(@PathVariable Long id) {
        boolean flag = sysUserService.resetPassword(id);
        return flag ? Result.success("密码已重置为123456") : Result.fail("重置失败");
    }
}