package com.xiaomai.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("planting")
public class Planting {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String batchNo;
    private String base;
    private String plot;
    private Double area;
    private LocalDate date;
    private String fertilizer;
    private String operator;
    private Long createdBy;
    private LocalDateTime createdAt;
}