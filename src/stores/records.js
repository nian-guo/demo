import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 全业务表共享数据中心
 * — 种子→种植→加工→质检→销售 五表顺序流
 * — 后置表只能引用前置表中已存在的批次号
 * — 种子状态按最远已填写环节自动生成
 */
export const useRecordsStore = defineStore('records', () => {
  // ==================== 种子信息 ====================
  const seeds = ref([
    { id: 1, batchNo: 'WM2026-05-089 A3', variety: '豫麦 58', origin: '河南新乡', supplier: '新乡市种子公司', quantity: 45, createdAt: '2026-05-12', certNo: 'YZ-2026-0001', contact: '张经理', createdBy: 0 },
    { id: 2, batchNo: 'WM2026-05-088 B1', variety: '郑麦 9023', origin: '河南郑州', supplier: '河南省农科院', quantity: 32, createdAt: '2026-05-10', certNo: 'ZM-2026-0015', contact: '李研究员', createdBy: 0 },
    { id: 3, batchNo: 'WM2026-04-072 C5', variety: '周麦 36', origin: '河南周口', supplier: '周口市种业集团', quantity: 28, createdAt: '2026-04-28', certNo: 'ZM-2026-0022', contact: '王主管', createdBy: 0 },
    { id: 4, batchNo: 'WM2026-04-068 D2', variety: '百农 207', origin: '河南新乡', supplier: '百农种业有限公司', quantity: 56, createdAt: '2026-04-20', certNo: 'BN-2026-0008', contact: '赵总', createdBy: 0 },
    { id: 5, batchNo: 'WM2026-04-065 A7', variety: '豫麦 58', origin: '河南焦作', supplier: '焦作市种子站', quantity: 38, createdAt: '2026-04-15', certNo: 'YZ-2026-0012', contact: '刘站长', createdBy: 0 },
    { id: 6, batchNo: 'WM2026-03-051 E3', variety: '郑麦 9023', origin: '河南洛阳', supplier: '洛阳市农科所', quantity: 50, createdAt: '2026-03-22', certNo: 'ZM-2026-0030', contact: '陈主任', createdBy: 0 },
    { id: 7, batchNo: 'WM2026-03-048 F9', variety: '周麦 36', origin: '河南商丘', supplier: '商丘市种子公司', quantity: 22, createdAt: '2026-03-18', certNo: 'ZM-2026-0018', contact: '周经理', createdBy: 0 },
    { id: 8, batchNo: 'WM2026-03-045 G4', variety: '百农 207', origin: '河南安阳', supplier: '安阳市农科院', quantity: 41, createdAt: '2026-03-12', certNo: 'BN-2026-0015', contact: '孙主任', createdBy: 0 },
  ])
  let seedNextId = 9

  // ==================== 种植记录 ====================
  const plantings = ref([
    { id: 1, batchNo: 'WM2026-05-089 A3', base: '豫北试验田', plot: 'A-12', area: 45, date: '2026-05-15', fertilizer: '复合肥 20kg + 尿素 10kg', operator: '王建国', irrigation: '滴灌系统', pesticide: '生物防治', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
    { id: 2, batchNo: 'WM2026-03-048 F9', base: '豫中示范基地', plot: 'B-03', area: 32, date: '2026-03-20', fertilizer: '有机肥 30kg', operator: '李德才', irrigation: '喷灌系统', pesticide: '低毒农药', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
    { id: 3, batchNo: 'WM2026-04-068 D2', base: '豫南良种场', plot: 'C-08', area: 56, date: '2026-04-22', fertilizer: '复合肥 25kg', operator: '张明辉', irrigation: '滴灌系统', pesticide: '生物防治', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
    { id: 4, batchNo: 'WM2026-04-065 A7', base: '豫北试验田', plot: 'A-15', area: 38, date: '2026-04-16', fertilizer: '磷酸二铵 15kg', operator: '赵志强', irrigation: '漫灌', pesticide: '无', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
  ])
  let plantingNextId = 5

  // ==================== 加工记录 ====================
  const processings = ref([
    { id: 1, batchNo: 'WM2026-05-088 B1', type: '精选加工', date: '2026-05-12', process: '重力精选 + 风筛清选', inputQty: 35, outputQty: 32, rate: '91.4%', operator: '刘工', equipment: '5XZ-5型', workshop: '1号车间', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
    { id: 2, batchNo: 'WM2026-03-045 G4', type: '包衣处理', date: '2026-03-15', process: '种衣剂包衣 + 烘干', inputQty: 42, outputQty: 41, rate: '97.6%', operator: '陈工', equipment: '5BY-5型', workshop: '2号车间', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
    { id: 3, batchNo: 'WM2026-04-072 C5', type: '烘干处理', date: '2026-04-30', process: '低温烘干 35°C', inputQty: 30, outputQty: 28, rate: '93.3%', operator: '周工', equipment: '5HG-6型', workshop: '1号车间', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
  ])
  let processingNextId = 4

  // ==================== 质检记录 ====================
  const qualities = ref([
    { id: 1, batchNo: 'WM2026-04-072 C5', date: '2026-05-02', purity: 99.2, germination: 96.5, moisture: 12.3, reportNo: 'QJ-2026-0421', result: '合格', inspector: '张检验员', standard: 'GB 4404.1-2008', conclusion: '符合国家一级种子标准', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
    { id: 2, batchNo: 'WM2026-05-088 B1', date: '2026-05-14', purity: 98.8, germination: 95.0, moisture: 12.8, reportNo: 'QJ-2026-0512', result: '合格', inspector: '李检验员', standard: 'GB 4404.1-2008', conclusion: '符合国家二级种子标准', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
    { id: 3, batchNo: 'WM2026-03-045 G4', date: '2026-03-18', purity: 97.5, germination: 92.3, moisture: 13.5, reportNo: 'QJ-2026-0318', result: '不合格', inspector: '王检验员', standard: 'GB 4404.1-2008', conclusion: '水分超标，需重新加工', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
  ])
  let qualityNextId = 4

  // ==================== 销售记录 ====================
  const sales = ref([
    { id: 1, batchNo: 'WM2026-04-068 D2', customer: '鑫农种业有限公司', date: '2026-05-10', quantity: 56, price: 4200, total: 235200, destination: '山东济南', contact: '赵经理', contractNo: 'HT-2026-0089', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
    { id: 2, batchNo: 'WM2026-03-051 E3', customer: '丰收农业合作社', date: '2026-04-15', quantity: 50, price: 4100, total: 205000, destination: '河北石家庄', contact: '钱会长', contractNo: 'HT-2026-0072', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
    { id: 3, batchNo: 'WM2026-04-065 A7', customer: '隆平高科', date: '2026-05-05', quantity: 38, price: 4500, total: 171000, destination: '安徽合肥', contact: '孙主管', contractNo: 'HT-2026-0095', createdBy: 0, createdAt: '2026-03-01T00:00:00Z' },
  ])
  let salesNextId = 4

  // ==================== 批次状态自动计算 ====================
  const stageOrder = ['registered', 'planting', 'processing', 'inspecting', 'sold']
  const stageLabel = { registered: '已登记', planting: '种植中', processing: '加工中', inspecting: '质检中', sold: '已售出' }
  const stageType = { registered: '', planting: 'warning', processing: '', inspecting: 'info', sold: 'success' }

  function getBatchStatus(batchNo) {
    if (sales.value.some(r => r.batchNo === batchNo)) return 'sold'
    if (qualities.value.some(r => r.batchNo === batchNo)) return 'inspecting'
    if (processings.value.some(r => r.batchNo === batchNo)) return 'processing'
    if (plantings.value.some(r => r.batchNo === batchNo)) return 'planting'
    return 'registered'
  }

  function getStatusLabel(status) { return stageLabel[status] || status }
  function getStatusType(status) { return stageType[status] || 'info' }

  // ==================== 顺序流校验 ====================
  const prereqMap = {
    planting: { store: () => seeds.value, name: '种子信息' },
    processing: { store: () => plantings.value, name: '种植记录' },
    quality: { store: () => processings.value, name: '加工记录' },
    sales: { store: () => qualities.value, name: '质检记录' },
  }

  function getPrerequisiteName(table) { return prereqMap[table]?.name || '' }

  function getAvailableBatchNos(table) {
    const prereq = prereqMap[table]
    if (!prereq) return []
    return [...new Set(prereq.store().map(r => r.batchNo))]
  }

  function canAddToTable(table, batchNo) {
    const prereq = prereqMap[table]
    if (!prereq) return true
    return prereq.store().some(r => r.batchNo === batchNo)
  }

  // ==================== ID 生成 ====================
  function nextSeedId() { return seedNextId++ }
  function nextPlantingId() { return plantingNextId++ }
  function nextProcessingId() { return processingNextId++ }
  function nextQualityId() { return qualityNextId++ }
  function nextSalesId() { return salesNextId++ }

  return {
    seeds, plantings, processings, qualities, sales,
    stageOrder, stageLabel, stageType,
    getBatchStatus, getStatusLabel, getStatusType,
    prereqMap, getPrerequisiteName, getAvailableBatchNos, canAddToTable,
    nextSeedId, nextPlantingId, nextProcessingId, nextQualityId, nextSalesId,
  }
})
