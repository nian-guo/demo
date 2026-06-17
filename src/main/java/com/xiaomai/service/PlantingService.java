package com.xiaomai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomai.dao.Planting;
import java.util.List;

public interface PlantingService extends IService<Planting> {
    List<Planting> listAllPlantings();
    boolean addPlanting(Planting planting);
    boolean updatePlanting(Long id, Planting planting);
    boolean deletePlanting(Long id);
}