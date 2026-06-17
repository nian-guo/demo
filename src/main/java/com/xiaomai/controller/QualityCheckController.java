package com.xiaomai.controller;

import com.alibaba.excel.EasyExcel;
import com.xiaomai.dao.QualityCheck;
import com.xiaomai.service.QualityCheckService;
import com.xiaomai.config.Result;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/api/quality")
public class QualityCheckController {

    @Resource
    private QualityCheckService qualityCheckService;

    /** 查询全部质检记录 */
    @GetMapping("/list")
    public Result<T> list() {
        List<QualityCheck> list = qualityCheckService.listAllQualityCheck();
        return Result.success(list);
    }

    /** 新增加质检记录 */
    @PostMapping("/add")
    public Result<T> add(@RequestBody QualityCheck qualityCheck) {
        boolean success = qualityCheckService.addQualityCheck(qualityCheck);
        return success ? Result.success("新增成功") : Result.fail("新增质检记录失败");
    }

    /** 修改质检记录 */
    @PutMapping("/update/{id}")
    public Result<T> update(@PathVariable Long id, @RequestBody QualityCheck qualityCheck) {
        boolean success = qualityCheckService.updateQualityCheck(id, qualityCheck);
        return success ? Result.success("修改成功") : Result.fail("修改质检记录失败");
    }

    /** 删除质检记录 【已修复颠倒逻辑】 */
    @DeleteMapping("/delete/{id}")
    public Result<T> delete(@PathVariable Long id) {
        boolean success = qualityCheckService.deleteQualityCheck(id);
        // 修复前：success=true 返回fail | 修复后：正常逻辑
        return success ? Result.success("删除成功") : Result.fail("删除质检记录失败");
    }

    /** Excel导出（增加异常捕获） */
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        try {
            String fileName = URLEncoder.encode("质检记录报表", "UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            List<QualityCheck> dataList = qualityCheckService.listAllQualityCheck();
            EasyExcel.write(response.getOutputStream(), QualityCheck.class)
                    .sheet("质检记录")
                    .doWrite(dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}