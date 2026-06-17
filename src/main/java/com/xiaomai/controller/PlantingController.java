package com.xiaomai.controller;

import com.alibaba.excel.EasyExcel;
import com.xiaomai.dao.Planting;
import com.xiaomai.service.PlantingService;
import com.xiaomai.config.Result;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/api/planting")
public class PlantingController {
    // 规范：注入Service接口，不直接注入实现类
    @Resource
    private PlantingService plantingService;

    /**
     * 查询全部种植记录
     */
    @GetMapping("/list")
    public Result<T> list() {
        List<Planting> list = plantingService.listAllPlantings();
        return Result.success(list);
    }

    /**
     * 新增加种植记录
     */
    @PostMapping("/add")
    public Result<T> add(@RequestBody Planting planting) {
        boolean success = plantingService.addPlanting(planting);
        return success ? Result.success() : Result.fail("新增种植记录失败");
    }

    /**
     * 修改种植记录
     */
    @PutMapping("/update/{id}")
    public Result<T> update(@PathVariable Long id, @RequestBody Planting planting) {
        boolean success = plantingService.updatePlanting(id, planting);
        return success ? Result.success() : Result.fail("修改种植记录失败");
    }

    /**
     * 删除种植记录
     */
    @DeleteMapping("/delete/{id}")
    public Result<T> delete(@PathVariable Long id) {
        boolean success = plantingService.deletePlanting(id);
        return success ? Result.success() : Result.fail("删除种植记录失败");
    }

    /**
     * 种植记录Excel导出
     */
    @GetMapping("/export")
    public void exportPlantingExcel(HttpServletResponse response) throws IOException {
        String fileName = URLEncoder.encode("种植记录报表", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        List<Planting> plantingList = plantingService.listAllPlantings();
        EasyExcel.write(response.getOutputStream(), Planting.class)
                .sheet("种植记录")
                .doWrite(plantingList);
    }
}