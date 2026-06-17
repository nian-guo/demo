package com.xiaomai.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 姓名
    private String name;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 角色 admin/planter/processor/inspector/seller
    private String role;
    // 手机号
    private String phone;
    // 邮箱
    private String email;
    // 状态 active启用 disabled禁用
    private String status;
    // 创建日期
    private LocalDate createdAt;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
}