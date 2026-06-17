package com.xiaomai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomai.dao.mapper.ProcessingMapper;
import com.xiaomai.dao.Processing;
import com.xiaomai.service.ProcessingService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProcessingServiceImpl extends ServiceImpl<ProcessingMapper, Processing> implements ProcessingService {

    @Override
    public List<Processing> listAllProcessing() {
        return list();
    }

    @Override
    public boolean addProcessing(Processing processing) {
        // 自动计算出成率
        BigDecimal input = processing.getInputQty();
        BigDecimal output = processing.getOutputQty();
        if (input != null && input.compareTo(BigDecimal.ZERO) > 0) {
            double rate = output.divide(input, 4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
            processing.setRate(String.format("%.1f%%", rate));
        } else {
            processing.setRate("-");
        }
        return save(processing);
    }

    @Override
    public boolean updateProcessing(Long id, Processing processing) {
        processing.setId(id);
        // 编辑时重新计算出成率
        BigDecimal input = processing.getInputQty();
        BigDecimal output = processing.getOutputQty();
        if (input != null && input.compareTo(BigDecimal.ZERO) > 0) {
            double rate = output.divide(input, 4, BigDecimal.ROUND_HALF_UP).doubleValue() * 100;
            processing.setRate(String.format("%.1f%%", rate));
        } else {
            processing.setRate("-");
        }
        return updateById(processing);
    }

    @Override
    public boolean deleteProcessing(Long id) {
        return removeById(id);
    }

    @Override
    public List<Processing> getByBatchNo(String batchNo) {
        LambdaQueryWrapper<Processing> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Processing::getBatchNo, batchNo);
        return list(wrapper);
    }
}