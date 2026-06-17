package com.xiaomai.vo;

import lombok.Data;
import java.util.List;

/**
 * 追溯时间线节点
 */
@Data
public class TraceStepVO {
    /** 环节标题 */
    private String title;
    /** 日期 */
    private String date;
    /** 描述 */
    private String description;
    /** 标签 */
    private List<String> tags;
    /** 是否完成 */
    private Boolean completed;
    /** 是否当前进行中节点 */
    private Boolean current;
}