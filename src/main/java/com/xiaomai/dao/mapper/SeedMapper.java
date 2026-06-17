package com.xiaomai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaomai.dao.Seed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SeedMapper extends BaseMapper<Seed> {
    @Select("select batch_no from seed")
    List<String> listAllBatchNos();
}