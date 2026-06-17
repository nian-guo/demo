package com.xiaomai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomai.dao.Seed;
import java.util.List;

public interface SeedService extends IService<Seed> {
    List<Seed> listAllSeed();
    boolean addSeed(Seed seed);
    boolean updateSeed(Long id, Seed seed);
    boolean deleteSeed(Long id);
    List<String> getBatchNos();
}