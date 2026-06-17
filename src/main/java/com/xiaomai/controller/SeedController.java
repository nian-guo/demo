package com.xiaomai.controller;

import com.alibaba.excel.EasyExcel;
import com.xiaomai.dao.Seed;
import com.xiaomai.service.SeedService;
import com.xiaomai.config.Result;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/api/seed")
public class SeedController {
    // 规范：注入接口，而非实现类
    @Resource
    private SeedService seedService;

    /**
     * 查询全部种子列表
     */
    @GetMapping("/list")
    public Result<T> list() {
        List<Seed> seedList = seedService.listAllSeed();
        return Result.success(seedList);
    }

    /**
     * 获取所有种子批次号（种植/加工下拉使用）
     */
    @GetMapping("/batch-nos")
    public Result<T> batchNos() {
        return Result.success(seedService.getBatchNos());
    }

    /**
     * 新增种子
     */
    @PostMapping("/add")
    public Result<T> add(@RequestBody Seed seed) {
        boolean ok = seedService.addSeed(seed);
        return ok ? Result.success() : Result.fail("新增种子失败");
    }

    /**
     * 编辑种子
     */
    @PutMapping("/update/{id}")
    public Result<T> update(@PathVariable Long id, @RequestBody Seed seed) {
        boolean ok = seedService.updateSeed(id, seed);
        return ok ? Result.success() : Result.fail("修改种子失败");
    }

    /**
     * 删除种子
     */
    @DeleteMapping("/delete/{id}")
    public Result<T> delete(@PathVariable Long id) {
        boolean ok = seedService.deleteSeed(id);
        return ok ? Result.success() : Result.fail("删除种子失败");
    }

    /**
     * 种子Excel导出接口
     */
    @GetMapping("/export")
    public void exportSeedExcel(HttpServletResponse response) throws IOException {
        String fileName = URLEncoder.encode("种子信息报表", "UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        List<Seed> seedList = seedService.listAllSeed();
        EasyExcel.write(response.getOutputStream(), Seed.class)
                .sheet("种子数据")
                .doWrite(seedList);
    }
}