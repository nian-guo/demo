package com.xiaomai.dao;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("seed")
public class Seed {
    @TableId(type = IdType.AUTO)
    // Excel表头：序号（列索引0）
    @ExcelProperty(value = "序号", index = 0)
    private Long id;

    // Excel表头：种子批次号（列索引1）
    @ExcelProperty(value = "种子批次号", index = 1)
    private String batchNo;

    // Excel表头：种子名称（列索引2）
    @ExcelProperty(value = "种子名称", index = 2)
    private String name;

    // Excel表头：品种（列索引3）
    @ExcelProperty(value = "品种", index = 3)
    private String variety;

    // Excel表头：产地（列索引4）
    @ExcelProperty(value = "产地", index = 4)
    private String origin;

    // Excel表头：创建时间（列索引5）
    @ExcelProperty(value = "创建时间", index = 5)
    private LocalDateTime createTime;
}