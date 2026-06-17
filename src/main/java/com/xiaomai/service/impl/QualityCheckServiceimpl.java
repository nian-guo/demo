package com.xiaomai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaomai.dao.QualityCheck;
import com.xiaomai.dao.mapper.QualityCheckMapper;
import com.xiaomai.service.QualityCheckService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QualityCheckServiceimpl extends ServiceImpl<QualityCheckMapper, QualityCheck>
        implements QualityCheckService {

    @Override
    public List<QualityCheck> listAllQualityCheck() {
        return list();
    }

    @Override
    public boolean addQualityCheck(QualityCheck qualityCheck) {
        return save(qualityCheck);
    }

    @Override
    public boolean updateQualityCheck(Long id, QualityCheck qualityCheck) {
        qualityCheck.setId(id);
        return updateById(qualityCheck);
    }

    @Override
    public boolean deleteQualityCheck(Long id) {
        return removeById(id);
    }
}