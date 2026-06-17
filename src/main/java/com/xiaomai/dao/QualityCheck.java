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
@TableName("quality_check")
public class QualityCheck {
    @TableId(type = IdType.AUTO)
    @ExcelProperty("序号")
    private Long id;

    @ExcelProperty("种子批次号")
    private String batchNo;

    @ExcelProperty("检测日期")
    private LocalDate checkDate;

    @ExcelProperty("纯度(%)")
    private BigDecimal purity;

    @ExcelProperty("发芽率(%)")
    private BigDecimal germinationRate;

    @ExcelProperty("水分(%)")
    private BigDecimal moisture;

    @ExcelProperty("检测报告号")
    private String reportNo;

    @ExcelProperty("检测结果")
    private String result;

    @ExcelProperty("检测员")
    private String checker;

    @ExcelProperty("复检备注")
    private String remark;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}