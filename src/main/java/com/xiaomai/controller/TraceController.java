package com.xiaomai.controller;

import com.xiaomai.config.Result;
import com.xiaomai.service.TraceService;
import com.xiaomai.vo.TraceVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

/**
 * 种子追溯查询控制器
 */
@RestController
@RequestMapping("/api/trace")
public class TraceController {

    @Resource
    private TraceService traceService;

    /**
     * 根据批次号查询全流程追溯信息
     * @param batchNo 种子批次号
     * @return 追溯数据
     */
    @GetMapping("/query")
    public Result<TraceVO> queryTrace(@RequestParam String batchNo) {
        if (batchNo == null || batchNo.trim().isEmpty()) {
            return Result.fail("追溯码/批次号不能为空");
        }
        TraceVO traceVO = traceService.getSeedTrace(batchNo.trim());
        if (traceVO == null) {
            return Result.fail("未查询到该批次的追溯信息");
        }
        return Result.success(traceVO);
    }
}