package com.xiaomai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomai.dao.Planting;
import com.xiaomai.dao.mapper.PlantingMapper;
import com.xiaomai.service.PlantingService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlantingServiceImpl extends ServiceImpl<PlantingMapper, Planting> implements PlantingService {

    @Override
    public List<Planting> listAllPlantings() {
        return list();
    }

    @Override
    public boolean addPlanting(Planting planting) {
        return save(planting);
    }

    @Override
    public boolean updatePlanting(Long id, Planting planting) {
        planting.setId(id);
        return updateById(planting);
    }

    @Override
    public boolean deletePlanting(Long id) {
        return removeById(id);
    }
}