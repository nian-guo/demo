package com.xiaomai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomai.dao.SalesRecord;
import com.xiaomai.dao.mapper.SalesRecordMapper;
import com.xiaomai.service.SalesRecordService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SalesRecordServiceImpl extends ServiceImpl<SalesRecordMapper, SalesRecord>
        implements SalesRecordService {

    @Override
    public List<SalesRecord> listAllSales() {
        return list();
    }

    @Override
    public boolean addSales(SalesRecord salesRecord) {
        // 自动计算总金额（数量 * 单价）
        BigDecimal total = salesRecord.getQuantity().multiply(salesRecord.getPrice());
        salesRecord.setTotalAmount(total);
        return save(salesRecord);
    }

    @Override
    public boolean updateSales(Long id, SalesRecord salesRecord) {
        // 自动重算总金额
        BigDecimal total = salesRecord.getQuantity().multiply(salesRecord.getPrice());
        salesRecord.setTotalAmount(total);
        salesRecord.setId(id);
        return updateById(salesRecord);
    }

    @Override
    public boolean deleteSales(Long id) {
        return removeById(id);
    }
}