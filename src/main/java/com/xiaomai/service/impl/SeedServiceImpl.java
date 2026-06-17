package com.xiaomai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomai.dao.Seed;
import com.xiaomai.dao.mapper.SeedMapper;
import com.xiaomai.service.SeedService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SeedServiceImpl extends ServiceImpl<SeedMapper, Seed> implements SeedService {

    @Override
    public List<Seed> listAllSeed() {
        return list();
    }

    @Override
    public boolean addSeed(Seed seed) {
        return save(seed);
    }

    @Override
    public boolean updateSeed(Long id, Seed seed) {
        seed.setId(id);
        return updateById(seed);
    }

    @Override
    public boolean deleteSeed(Long id) {
        return removeById(id);
    }

    @Override
    public List<String> getBatchNos() {
        return baseMapper.listAllBatchNos();
    }
}