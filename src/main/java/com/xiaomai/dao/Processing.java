package com.xiaomai.dao;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("processing")
public class Processing {
    @TableId(type = IdType.AUTO)
    @ExcelProperty("序号")
    private Long id;

    @ExcelProperty("种子批次号")
    private String batchNo;

    @ExcelProperty("加工类型")
    private String type;

    @ExcelProperty("加工日期")
    private LocalDate date;

    @ExcelProperty("加工工艺")
    private String process;

    @ExcelProperty("投入量(吨)")
    private BigDecimal inputQty;

    @ExcelProperty("产出量(吨)")
    private BigDecimal outputQty;

    @ExcelProperty("出成率")
    private String rate;

    @ExcelProperty("操作人")
    private String operator;

    // 详情弹窗字段，表格不渲染
    @ExcelProperty("设备型号")
    private String equipment;
    @ExcelProperty("加工车间")
    private String workshop;

    // 创建/更新时间，数据库自动填充，前端无需传入
    private LocalDateTime createdAt;
    private LocalDateTime updateTime;
}