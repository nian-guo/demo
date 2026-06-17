package com.xiaomai.service.impl;

import com.xiaomai.dao.Planting;
import com.xiaomai.dao.QualityCheck;
import com.xiaomai.dao.SalesRecord;
import com.xiaomai.dao.Seed;
import com.xiaomai.dao.mapper.PlantingMapper;
import com.xiaomai.dao.mapper.QualityCheckMapper;
import com.xiaomai.dao.mapper.SalesRecordMapper;
import com.xiaomai.dao.mapper.SeedMapper;
import com.xiaomai.service.TraceService;
import com.xiaomai.vo.TraceStepVO;
import com.xiaomai.vo.TraceVO;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TraceServiceImpl implements TraceService {

    @Resource
    private SeedMapper seedMapper;

    @Resource
    private PlantingMapper plantingMapper;

    @Resource
    private QualityCheckMapper qualityCheckMapper;

    @Resource
    private SalesRecordMapper salesRecordMapper;

    @Override
    public TraceVO getSeedTrace(String batchNo) {
        TraceVO traceVO = new TraceVO();
        // 1. 查询种子基础信息
        Seed seed = seedMapper.selectList(null).stream()
                .filter(s -> s.getBatchNo().equals(batchNo))
                .findFirst()
                .orElse(null);
        if (seed == null) {
            return null;
        }
        // 基础信息赋值
        traceVO.setBatchNo(seed.getBatchNo());
        traceVO.setVariety(seed.getVariety());
        traceVO.setOrigin(seed.getOrigin());
        traceVO.setSupplier("新乡市种子公司");

        // 2. 依次查询各环节数据
        List<Planting> plantingList = plantingMapper.selectList(null).stream()
                .filter(p -> p.getBatchNo().equals(batchNo))
                .collect(Collectors.toList());

        List<QualityCheck> qualityList = qualityCheckMapper.selectList(null).stream()
                .filter(q -> q.getBatchNo().equals(batchNo))
                .collect(Collectors.toList());

        List<SalesRecord> salesList = salesRecordMapper.selectList(null).stream()
                .filter(s -> s.getBatchNo().equals(batchNo))
                .collect(Collectors.toList());

        // 3. 组装时间线
        List<TraceStepVO> timeline = new ArrayList<>();
        // 节点1：品种登记（固定节点）
        TraceStepVO step1 = new TraceStepVO();
        step1.setTitle("品种登记");
        step1.setDate("2026-01-15");
        step1.setDescription("品种" + seed.getVariety() + "完成系统登记，来源为新乡市种子公司");
        step1.setTags(Arrays.asList("品种认证", seed.getVariety()));
        step1.setCompleted(true);
        step1.setCurrent(false);
        timeline.add(step1);

        // 节点2：种植环节（修复空指针，完整代码）
        if (!plantingList.isEmpty()) {
            Planting planting = plantingList.get(0);
            TraceStepVO step2 = new TraceStepVO();
            step2.setTitle("种植环节");
            // 日期空值保护，防止NullPointerException
            LocalDate plantDate = planting.getDate() == null ? LocalDate.now() : LocalDate.ofEpochDay(planting.getDate().toEpochDay());
            step2.setDate(plantDate.toString());
            step2.setDescription("在试验田完成播种，种植面积：" + planting.getArea() + "亩");
            step2.setTags(Arrays.asList("试验田", planting.getPlot()));
            step2.setCompleted(true);
            step2.setCurrent(false);
            timeline.add(step2);

            // 节点3：田间管理（拓展节点）
            TraceStepVO step3 = new TraceStepVO();
            step3.setTitle("田间管理");
            step3.setDate(plantDate + " ~ 2026-05-10");
            step3.setDescription("施肥：" + planting.getFertilizer() + "；病虫害防治");
            step3.setTags(Arrays.asList("施肥", "病虫害防治"));
            step3.setCompleted(true);
            step3.setCurrent(false);
            timeline.add(step3);
        }

        // 节点4：加工环节
        TraceStepVO step4 = new TraceStepVO();
        step4.setTitle("加工环节");
        step4.setDate("2026-05-12");
        step4.setDescription("重力精选 + 风筛清选，出成率 91.4%");
        step4.setTags(Arrays.asList("精选加工", "91.4%"));
        step4.setCompleted(true);
        step4.setCurrent(false);
        timeline.add(step4);

        // 节点5：质检环节
        if (!qualityList.isEmpty()) {
            QualityCheck quality = qualityList.get(0);
            TraceStepVO step5 = new TraceStepVO();
            step5.setTitle("质量检测");
            LocalDate checkDate = quality.getCheckDate() == null ? LocalDate.now() : quality.getCheckDate();
            step5.setDate(checkDate.toString());
            step5.setDescription("纯度 " + quality.getPurity() + "%，发芽率 " + quality.getGerminationRate() + "%，水分 " + quality.getMoisture() + "%"
                    + "，检测报告号 " + quality.getReportNo());
            step5.setTags(Arrays.asList(quality.getResult(), quality.getPurity() + "%"));
            step5.setCompleted(true);
            step5.setCurrent(false);
            timeline.add(step5);
        }

        // 节点6：销售环节 + 判断整体状态
        TraceStepVO step6 = new TraceStepVO();
        step6.setTitle("销售出库");
        if (!salesList.isEmpty()) {
            SalesRecord sales = salesList.get(0);
            LocalDate saleDate = sales.getSalesDate() == null ? LocalDate.now() : sales.getSalesDate();
            step6.setDate(saleDate.toString());
            step6.setDescription("已完成销售，客户：" + sales.getCustomer() + "，销售地区：" + sales.getDestination());
            step6.setTags(Arrays.asList("已售出"));
            step6.setCompleted(true);
            step6.setCurrent(false);
            // 整体状态：已售
            traceVO.setStatus("sold");
            traceVO.setStatusText("已售出");
        } else {
            step6.setDate("待出库");
            step6.setDescription("等待销售出库，客户待定");
            step6.setTags(Arrays.asList("待售"));
            step6.setCompleted(false);
            step6.setCurrent(true);
            // 整体状态：销售中
            traceVO.setStatus("selling");
            traceVO.setStatusText("销售中");
        }
        timeline.add(step6);

        traceVO.setTimeline(timeline);
        return traceVO;
    }
}