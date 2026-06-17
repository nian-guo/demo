<template>
  <div class="page-container dashboard">
    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h1 class="page-title">数据看板</h1>
        <p class="page-desc">小麦种子全链路追溯数据概览</p>
      </div>
      <div class="header-actions">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          size="default"
          class="date-picker"
        />
        <el-button type="primary" :icon="Refresh" @click="refreshData">刷新数据</el-button>
      </div>
    </div>

    <!-- Stats Row -->
    <div class="stats-grid">
      <StatCard
        icon="Collection"
        label="种子批次总数"
        :value="stats.totalBatches"
        unit="批"
        :trend="8.5"
        iconBg="#d4a853"
        description="较上月新增 23 批"
      />
      <StatCard
        icon="Sunny"
        label="种植覆盖面积"
        :value="stats.plantingArea"
        unit="亩"
        :trend="12.3"
        iconBg="#5b8c5a"
        description="涉及 6 个种植基地"
      />
      <StatCard
        icon="Checked"
        label="质检合格率"
        :value="stats.qualityRate"
        unit="%"
        :trend="2.1"
        iconBg="#4a90d9"
        description="本年度累计检测"
      />
      <StatCard
        icon="Sell"
        label="累计销售量"
        :value="stats.totalSales"
        unit="吨"
        :trend="-3.2"
        iconBg="#e87a4a"
        description="已签合同 48 份"
      />
    </div>

    <!-- Charts Row -->
    <div class="charts-grid">
      <!-- Line Chart -->
      <div class="chart-card glass-light">
        <div class="chart-header">
          <h3>种子生产趋势</h3>
          <div class="chart-tabs">
            <button
              v-for="tab in ['月', '季', '年']"
              :key="tab"
              class="chart-tab"
              :class="{ active: activeTab === tab }"
              @click="activeTab = tab"
            >{{ tab }}</button>
          </div>
        </div>
        <div class="chart-body">
          <v-chart :option="trendChartOption" autoresize />
        </div>
      </div>

      <!-- Pie Chart -->
      <div class="chart-card glass-light">
        <div class="chart-header">
          <h3>品种分布统计</h3>
        </div>
        <div class="chart-body">
          <v-chart :option="pieChartOption" autoresize />
        </div>
      </div>
    </div>

    <!-- Bottom Row -->
    <div class="bottom-grid">
      <!-- Recent Activities -->
      <div class="chart-card glass-light">
        <div class="chart-header">
          <h3>最近操作记录</h3>
          <el-button text size="small" type="primary">查看全部</el-button>
        </div>
        <div class="activity-list">
          <div v-for="(act, i) in activities" :key="i" class="activity-item">
            <div class="activity-dot" :style="{ background: act.color }"></div>
            <div class="activity-content">
              <p class="activity-text">{{ act.text }}</p>
              <span class="activity-time">{{ act.time }}</span>
            </div>
            <span class="activity-badge" :style="{ background: act.color + '20', color: act.color }">
              {{ act.type }}
            </span>
          </div>
        </div>
      </div>

      <!-- Seed Status Map -->
      <div class="chart-card glass-light">
        <div class="chart-header">
          <h3>各环节种子数量</h3>
        </div>
        <div class="chart-body">
          <v-chart :option="barChartOption" autoresize />
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
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import {
  TitleComponent, TooltipComponent, LegendComponent,
  GridComponent, ToolboxComponent
} from 'echarts/components'
import { Refresh } from '@element-plus/icons-vue'
import StatCard from '@/components/common/StatCard.vue'

use([CanvasRenderer, LineChart, BarChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent, ToolboxComponent])

const dateRange = ref([])
const activeTab = ref('月')

const stats = reactive({
  totalBatches: 856,
  plantingArea: 12480,
  qualityRate: 98.7,
  totalSales: 2345
})

const activities = [
  { text: '新增种子批次 WM2026-05-089', time: '10 分钟前', type: '种植', color: '#5b8c5a' },
  { text: '批次 WM2026-04-072 完成质检', time: '32 分钟前', type: '质检', color: '#4a90d9' },
  { text: '销售商 鑫农种业 下单 50 吨', time: '1 小时前', type: '销售', color: '#e87a4a' },
  { text: '加工完成批次 WM2026-04-068', time: '2 小时前', type: '加工', color: '#8b6914' },
  { text: '新增种植基地：豫北试验田', time: '3 小时前', type: '管理', color: '#d4a853' },
]

const trendChartOption = ref({
  tooltip: {
    trigger: 'axis',
    backgroundColor: '#1a1a1a',
    borderColor: '#2a2822',
    textStyle: { color: '#f5f0e8', fontSize: 12 }
  },
  grid: { left: 0, right: 8, top: 8, bottom: 0, containLabel: true },
  xAxis: {
    type: 'category',
    data: ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月'],
    axisLine: { lineStyle: { color: '#2a2822' } },
    axisTick: { show: false },
    axisLabel: { color: '#6b6457', fontSize: 11 }
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: '#1e1e1e' } },
    axisLabel: { color: '#6b6457', fontSize: 11 }
  },
  series: [{
    data: [42, 55, 48, 70, 82, 68, 95, 88, 75, 90, 105, 86],
    type: 'line',
    smooth: true,
    symbol: 'circle',
    symbolSize: 4,
    lineStyle: { width: 2.5, color: '#d4a853' },
    itemStyle: { color: '#d4a853' },
    areaStyle: {
      color: {
        type: 'linear',
        x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(212,168,83,0.2)' },
          { offset: 1, color: 'rgba(212,168,83,0)' }
        ]
      }
    }
  }]
})

const pieChartOption = ref({
  tooltip: {
    trigger: 'item',
    backgroundColor: '#1a1a1a',
    borderColor: '#2a2822',
    textStyle: { color: '#f5f0e8', fontSize: 12 }
  },
  series: [{
    type: 'pie',
    radius: ['55%', '78%'],
    center: ['50%', '50%'],
    avoidLabelOverlap: false,
    itemStyle: { borderRadius: 6, borderColor: '#0f0f0f', borderWidth: 3 },
    label: { show: false },
    emphasis: {
      label: { show: true, fontSize: 14, fontWeight: 'bold' },
      scaleSize: 8
    },
    data: [
      { value: 320, name: '豫麦 58', itemStyle: { color: '#d4a853' } },
      { value: 245, name: '郑麦 9023', itemStyle: { color: '#5b8c5a' } },
      { value: 168, name: '周麦 36', itemStyle: { color: '#4a90d9' } },
      { value: 123, name: '百农 207', itemStyle: { color: '#e87a4a' } },
    ]
  }]
})

const barChartOption = ref({
  tooltip: {
    trigger: 'axis',
    backgroundColor: '#1a1a1a',
    borderColor: '#2a2822',
    textStyle: { color: '#f5f0e8', fontSize: 12 }
  },
  grid: { left: 0, right: 8, top: 8, bottom: 0, containLabel: true },
  xAxis: {
    type: 'category',
    data: ['品种登记', '种植中', '加工中', '质检中', '待销售', '已售出'],
    axisLine: { lineStyle: { color: '#2a2822' } },
    axisTick: { show: false },
    axisLabel: { color: '#6b6457', fontSize: 10 }
  },
  yAxis: {
    type: 'value',
    splitLine: { lineStyle: { color: '#1e1e1e' } },
    axisLabel: { color: '#6b6457', fontSize: 11 }
  },
  series: [{
    data: [856, 523, 410, 368, 295, 248],
    type: 'bar',
    barWidth: 24,
    itemStyle: {
      borderRadius: [6, 6, 0, 0],
      color: {
        type: 'linear',
        x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(212,168,83,0.9)' },
          { offset: 1, color: 'rgba(212,168,83,0.3)' }
        ]
      }
    },
    emphasis: {
      itemStyle: { color: '#d4a853' }
    }
  }]
})

function refreshData() {
  // 模拟刷新数据
  stats.totalBatches = 856 + Math.floor(Math.random() * 10)
}
</script>

<style scoped>
.dashboard {
  max-width: var(--page-max-width);
}
.dashboard .chart-body {
  height: 320px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: 28px;
  flex-wrap: wrap;
  gap: 16px;
}

.page-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.02em;
  margin-bottom: 4px;
}

.page-desc {
  font-size: 0.8125rem;
  color: var(--text-muted);
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.date-picker {
  width: 260px;
}

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
  margin-bottom: 22px;
}

@media (max-width: 1100px) {
  .stats-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 600px) {
  .stats-grid { grid-template-columns: 1fr; }
}

/* Charts Grid */
.charts-grid {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 18px;
  margin-bottom: 22px;
}

@media (max-width: 1100px) {
  .charts-grid { grid-template-columns: 1fr; }
}

/* Bottom Grid */
.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}

@media (max-width: 1100px) {
  .bottom-grid { grid-template-columns: 1fr; }
}

/* Chart Card */
.chart-card {
  border-radius: 16px;
  padding: 24px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  transition: all 0.3s var(--transition-smooth);
}

.chart-card:hover {
  border-color: var(--border-light);
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.chart-header h3 {
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

.chart-tabs {
  display: flex;
  background: var(--bg-surface);
  border-radius: 8px;
  padding: 2px;
  gap: 2px;
}

.chart-tab {
  padding: 5px 14px;
  font-size: 0.75rem;
  font-weight: 500;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--text-muted);
  cursor: pointer;
  transition: all 0.25s;
}

.chart-tab.active {
  background: var(--primary);
  color: #1a1a1a;
}

.chart-body {
  height: 280px;
  width: 100%;
}

/* Activity List */
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid rgba(42, 40, 34, 0.5);
  transition: all 0.2s;
}

.activity-item:last-child {
  border-bottom: none;
}

.activity-item:hover {
  padding-left: 4px;
}

.activity-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.activity-content {
  flex: 1;
  min-width: 0;
}

.activity-text {
  font-size: 0.8125rem;
  color: var(--text-primary);
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.activity-time {
  font-size: 0.6875rem;
  color: var(--text-muted);
}

.activity-badge {
  font-size: 0.6875rem;
  padding: 3px 8px;
  border-radius: 4px;
  font-weight: 500;
  white-space: nowrap;
  flex-shrink: 0;
}
</style>
