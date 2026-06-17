export function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
}

export function formatDateTime(date) {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString('zh-CN')
}

export function traceStatusTag(status) {
  const map = {
    planting: { text: '种植中', color: '#5b8c5a' },
    processing: { text: '加工中', color: '#4a90d9' },
    inspecting: { text: '质检中', color: '#e8c44a' },
    selling: { text: '销售中', color: '#e87a4a' },
    sold: { text: '已售出', color: '#909399' }
  }
  return map[status] || { text: status, color: '#909399' }
}
