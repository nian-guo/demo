package com.xiaomai.controller;

import com.alibaba.excel.EasyExcel;
import com.xiaomai.dao.SalesRecord;
import com.xiaomai.service.SalesRecordService;
import com.xiaomai.config.Result;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SalesRecordController {

    @Resource
    private SalesRecordService salesRecordService;

    /**
     * 查询所有销售记录
     */
    @GetMapping("/list")
    public Result<T> list() {
        List<SalesRecord> list = salesRecordService.listAllSales();
        return Result.success(list);
    }

    /**
     * 新增销售记录
     */
    @PostMapping("/add")
    public Result<T> add(@RequestBody SalesRecord salesRecord) {
        boolean success = salesRecordService.addSales(salesRecord);
        return success ? Result.success("新增销售记录成功") : Result.fail("新增销售记录失败");
    }

    /**
     * 修改销售记录
     */
    @PutMapping("/update/{id}")
    public Result<T> update(@PathVariable Long id, @RequestBody SalesRecord salesRecord) {
        boolean success = salesRecordService.updateSales(id, salesRecord);
        return success ? Result.success("修改销售记录成功") : Result.fail("修改销售记录失败");
    }

    /**
     * 删除销售记录（逻辑已修正）
     */
    @DeleteMapping("/delete/{id}")
    public Result<T> delete(@PathVariable Long id) {
        boolean success = salesRecordService.deleteSales(id);
        return success ? Result.success("删除销售记录成功") : Result.fail("删除销售记录失败");
    }

    /**
     * 销售记录 Excel 导出
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        try {
            String fileName = URLEncoder.encode("小麦种子销售报表", "UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            List<SalesRecord> dataList = salesRecordService.listAllSales();
            EasyExcel.write(response.getOutputStream(), SalesRecord.class)
                    .sheet("销售记录")
                    .doWrite(dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}