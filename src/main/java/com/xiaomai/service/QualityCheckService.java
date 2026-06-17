package com.xiaomai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomai.dao.QualityCheck;
import java.util.List;

public interface QualityCheckService extends IService<QualityCheck> {
    List<QualityCheck> listAllQualityCheck();
    boolean addQualityCheck(QualityCheck qualityCheck);
    boolean updateQualityCheck(Long id, QualityCheck qualityCheck);
    boolean deleteQualityCheck(Long id);
}