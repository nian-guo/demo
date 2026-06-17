package com.xiaomai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomai.dao.Processing;
import java.util.List;

public interface ProcessingService extends IService<Processing> {
    // 查询全部加工记录
    List<Processing> listAllProcessing();
    // 新增加工记录
    boolean addProcessing(Processing processing);
    // 修改加工记录
    boolean updateProcessing(Long id, Processing processing);
    // 删除加工记录
    // 根据批次号查询加工记录（前端批次校验使用）
    boolean deleteProcessing(Long id);
    List<Processing> getByBatchNo(String batchNo);
}