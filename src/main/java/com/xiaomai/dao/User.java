package com.xiaomai.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String name;
    private String role;
    private String phone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime; // 补全字段

    // 补全setUpdateTime方法（适配原有逻辑）
    public void setUpdateTime(LocalDateTime now) {
        this.updateTime = now;
    }
}