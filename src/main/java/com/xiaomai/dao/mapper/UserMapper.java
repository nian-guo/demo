package com.xiaomai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomai.dao.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}