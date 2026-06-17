package com.xiaomai.service;

import com.xiaomai.vo.TraceVO;

/**
 * 种子追溯服务接口
 */
public interface TraceService {

    /**
     * 根据批次号查询全流程追溯信息
     * @param batchNo 种子批次号
     * @return 追溯数据
     */
    TraceVO getSeedTrace(String batchNo);
}