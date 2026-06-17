package com.xiaomai.vo;

import lombok.Data;
import java.util.List;

/**
 * 种子全追溯返回视图
 */
@Data
public class TraceVO {
    /** 种子批次号 */
    private String batchNo;
    /** 品种 */
    private String variety;
    /** 产地 */
    private String origin;
    /** 供种单位 */
    private String supplier;
    /** 状态编码：planting/processing/inspecting/selling/sold */
    private String status;
    /** 状态文本 */
    private String statusText;
    /** 全流程时间线 */
    private List<TraceStepVO> timeline;
}