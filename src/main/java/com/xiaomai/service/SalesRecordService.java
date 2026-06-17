package com.xiaomai.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaomai.dao.SalesRecord;
import java.util.List;

public interface SalesRecordService extends IService<SalesRecord> {

    /** 查询全部销售记录 */
    List<SalesRecord> listAllSales();

    /** 新增销售记录 */
    boolean addSales(SalesRecord salesRecord);

    /** 根据ID修改销售记录 */
    boolean updateSales(Long id, SalesRecord salesRecord);

    /** 根据ID删除销售记录 */
    boolean deleteSales(Long id);
}