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
@TableName("sales_record")
public class SalesRecord {

    @TableId(type = IdType.AUTO)
    @ExcelProperty("序号")
    private Long id;

    @ExcelProperty("种子批次号")
    private String batchNo;

    @ExcelProperty("客户名称")
    private String customer;

    @ExcelProperty("销售日期")
    private LocalDate salesDate;

    @ExcelProperty("数量(吨)")
    private BigDecimal quantity;

    @ExcelProperty("单价(元/吨)")
    private BigDecimal price;

    @ExcelProperty("总金额(元)")
    private BigDecimal totalAmount;

    @ExcelProperty("销售地区")
    private String destination;

    @ExcelProperty("合同编号")
    private String contractNo;

    @ExcelProperty("联系人")
    private String contact;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}