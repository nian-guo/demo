<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h1>统计报表</h1>
        <p>小麦种子追溯数据统计与分析</p>
      </div>
      <div class="header-actions">
        <el-date-picker v-model="dateRange" type="monthrange" range-separator="至" start-placeholder="开始月份" end-placeholder="结束月份" class="date-picker" />
        <el-button type="primary" :icon="Download">导出报表</el-button>
      </div>
    </div>

    <!-- Summary Cards -->
    <div class="report-stats">
      <div class="report-stat glass-light">
        <span class="rs-value">{{ reportData.totalOutput }}</span>
        <span class="rs-label">总产量 (吨)</span>
      </div>
      <div class="report-stat glass-light">
        <span class="rs-value">{{ reportData.avgQuality }}</span>
        <span class="rs-label">平均合格率</span>
      </div>
      <div class="report-stat glass-light">
        <span class="rs-value">{{ reportData.totalRevenue }}</span>
        <span class="rs-label">销售总额 (万元)</span>
      </div>
      <div class="report-stat glass-light">
        <span class="rs-value">{{ reportData.activeBases }}</span>
        <span class="rs-label">活跃种植基地</span>
      </div>
    </div>

    <!-- Charts -->
    <div class="report-charts">
      <div class="chart-card glass-light">
        <div class="chart-header"><h3>各品种产量对比</h3></div>
        <div class="chart-body">
          <v-chart :option="yieldChartOption" autoresize />
        </div>
      </div>
      <div class="chart-card glass-light">
        <div class="chart-header"><h3>月度销售趋势</h3></div>
        <div class="chart-body">
          <v-chart :option="salesChartOption" autoresize />
        </div>
      </div>
      <div class="chart-card glass-light">
        <div class="chart-header"><h3>各基地质检合格率</h3></div>
        <div class="chart-body">
          <v-chart :option="qualityChartOption" autoresize />
        </div>
      </div>
      <div class="chart-card glass-light">
        <div class="chart-header"><h3>品种种植面积分布</h3></div>
        <div class="chart-body">
          <v-chart :option="areaChartOption" autoresize />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'
import { Download } from '@element-plus/icons-vue'

use([CanvasRenderer, LineChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const dateRange = ref([])

const reportData = reactive({
  totalOutput: '2,345',
  avgQuality: '98.7%',
  totalRevenue: '963.5',
  activeBases: '6'
})

const chartStyle = {
  tooltip: { backgroundColor: '#1a1a1a', borderColor: '#2a2822', textStyle: { color: '#f5f0e8', fontSize: 12 } },
  grid: { left: 0, right: 8, top: 8, bottom: 0, containLabel: true },
}

const yieldChartOption = ref({
  ...chartStyle,
  xAxis: { type: 'category', data: ['豫麦58', '郑麦9023', '周麦36', '百农207'], axisLine: { lineStyle: { color: '#2a2822' } }, axisTick: { show: false }, axisLabel: { color: '#6b6457', fontSize: 11 } },
  yAxis: { type: 'value', splitLine: { lineStyle: { color: '#1e1e1e' } }, axisLabel: { color: '#6b6457' } },
  series: [{ data: [820, 645, 480, 400], type: 'bar', barWidth: 32, itemStyle: { borderRadius: [6,6,0,0], color: { type: 'linear', x:0,y:0,x2:0,y2:1, colorStops: [{offset:0,color:'rgba(212,168,83,0.9)'},{offset:1,color:'rgba(212,168,83,0.3)'}] } } }]
})

const salesChartOption = ref({
  ...chartStyle,
  xAxis: { type: 'category', data: ['1月','2月','3月','4月','5月','6月'], axisLine: { lineStyle: { color: '#2a2822' } }, axisTick: { show: false }, axisLabel: { color: '#6b6457', fontSize: 11 } },
  yAxis: { type: 'value', splitLine: { lineStyle: { color: '#1e1e1e' } }, axisLabel: { color: '#6b6457' } },
  series: [{ data: [85, 102, 130, 148, 165, 192], type: 'line', smooth: true, symbol: 'circle', symbolSize: 4, lineStyle: { width: 2.5, color: '#5b8c5a' }, itemStyle: { color: '#5b8c5a' }, areaStyle: { color: { type: 'linear', x:0,y:0,x2:0,y2:1, colorStops: [{offset:0,color:'rgba(91,140,90,0.2)'},{offset:1,color:'rgba(91,140,90,0)'}] } } }]
})

const qualityChartOption = ref({
  ...chartStyle,
  xAxis: { type: 'category', data: ['豫北试验田','豫中示范基地','豫南良种场','豫东农场','豫西育种中心'], axisLine: { lineStyle: { color: '#2a2822' } }, axisTick: { show: false }, axisLabel: { color: '#6b6457', fontSize: 9, rotate: 20 } },
  yAxis: { type: 'value', min: 90, splitLine: { lineStyle: { color: '#1e1e1e' } }, axisLabel: { color: '#6b6457' } },
  series: [{ data: [99.2, 98.8, 97.5, 98.1, 99.0], type: 'bar', barWidth: 24, itemStyle: { borderRadius: [6,6,0,0], color: { type: 'linear', x:0,y:0,x2:0,y2:1, colorStops: [{offset:0,color:'rgba(74,144,217,0.9)'},{offset:1,color:'rgba(74,144,217,0.3)'}] } } }]
})

const areaChartOption = ref({
  tooltip: { trigger: 'item', backgroundColor: '#1a1a1a', borderColor: '#2a2822', textStyle: { color: '#f5f0e8', fontSize: 12 } },
  series: [{
    type: 'pie', radius: ['50%', '75%'], center: ['50%', '50%'], itemStyle: { borderRadius: 6, borderColor: '#0f0f0f', borderWidth: 3 },
    label: { show: false },
    data: [
      { value: 4800, name: '豫麦58', itemStyle: { color: '#d4a853' } },
      { value: 3200, name: '郑麦9023', itemStyle: { color: '#5b8c5a' } },
      { value: 2600, name: '周麦36', itemStyle: { color: '#4a90d9' } },
      { value: 1880, name: '百农207', itemStyle: { color: '#e87a4a' } },
    ]
  }]
})
</script>

<style scoped>
.page-container { max-width: var(--page-max-width); }
.page-header { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 24px; flex-wrap: wrap; gap: 12px; }
.page-header h1 { font-size: 1.5rem; font-weight: 700; }
.page-header p { font-size: 0.8125rem; color: var(--text-muted); margin-top: 4px; }
.header-actions { display: flex; gap: 10px; }
.date-picker { width: 240px; }

.report-stats { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 14px; margin-bottom: 22px; }
@media (max-width: 800px) { .report-stats { grid-template-columns: repeat(2, minmax(0, 1fr)); } }

.report-stat { padding: 20px; border-radius: 14px; text-align: center; min-width: 0; }
.rs-value { display: block; font-size: 1.75rem; font-weight: 700; color: var(--primary); }
.rs-label { font-size: 0.75rem; color: var(--text-muted); margin-top: 4px; display: block; }

.report-charts { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 18px; }
@media (max-width: 900px) { .report-charts { grid-template-columns: minmax(0, 1fr); } }

.chart-card { border-radius: 16px; padding: 24px; background: var(--bg-card); border: 1px solid var(--border); min-width: 0; overflow: hidden; }
.chart-header { margin-bottom: 16px; }
.chart-header h3 { font-size: 0.9375rem; font-weight: 600; }
.chart-body { height: 260px; min-width: 0; }
</style>
