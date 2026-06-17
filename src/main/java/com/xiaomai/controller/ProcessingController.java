package com.xiaomai.controller;

import com.alibaba.excel.EasyExcel;
import com.xiaomai.dao.Processing;
import com.xiaomai.service.ProcessingService;
import com.xiaomai.config.Result;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/api/processing")
public class ProcessingController {

    @Resource
    private ProcessingService processingService;

    /**
     * 查询全部加工记录
     */
    @GetMapping("/list")
    public Result<T> list() {
        List<Processing> list = processingService.listAllProcessing();
        return Result.success(list);
    }

    /**
     * 新增加工记录
     */
    @PostMapping("/add")
    public Result<T> add(@RequestBody Processing processing) {
        boolean success = processingService.addProcessing(processing);
        return success ? Result.success() : Result.fail("新增加工记录失败");
    }

    /**
     * 修改加工记录
     */
    @PutMapping("/update/{id}")
    public Result<T> update(@PathVariable Long id, @RequestBody Processing processing) {
        boolean success = processingService.updateProcessing(id, processing);
        return success ? Result.success() : Result.fail("修改加工记录失败");
    }

    /**
     * 删除加工记录
     */
    @DeleteMapping("/delete/{id}")
    public Result<T> delete(@PathVariable Long id) {
        boolean success = processingService.deleteProcessing(id);
        return success ? Result.success() : Result.fail("删除加工记录失败");
    }

    /**
     * 根据批次号查询加工记录（前端校验）
     */
    @GetMapping("/batch/{batchNo}")
    public Result<T> getByBatch(@PathVariable String batchNo) {
        List<Processing> list = processingService.getByBatchNo(batchNo);
        return Result.success(list);
    }

    /**
     * 加工记录 Excel 导出
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        String fileName = URLEncoder.encode("加工记录报表", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        List<Processing> dataList = processingService.listAllProcessing();
        EasyExcel.write(response.getOutputStream(), Processing.class)
                .sheet("加工记录")
                .doWrite(dataList);
    }
}