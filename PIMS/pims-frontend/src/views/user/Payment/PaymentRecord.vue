<template>
  <div class="payment-record-page">
    <div class="page-header">
      <el-button @click="goBack" icon="ArrowLeft" text>返回</el-button>
      <h2>缴费记录</h2>
      <div class="placeholder"></div>
    </div>

    <!-- 查询区域 -->
    <el-card class="query-card" shadow="never">
      <el-form :model="queryForm" inline class="query-form">
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="queryForm.startDate"
            type="date"
            placeholder="选择开始日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="queryForm.endDate"
            type="date"
            placeholder="选择结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery" :loading="loading">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计区域 -->
    <el-card class="stat-card" shadow="never" v-if="totalAmount > 0">
      <div class="stat-content">
        <span class="stat-label">区间内缴费总额：</span>
        <span class="stat-value">￥{{ totalAmount }}</span>
      </div>
    </el-card>

    <!-- 缴费记录列表 -->
    <el-card class="record-list-card" shadow="never">
      <div v-if="recordList.length > 0" class="record-list">
        <div
          v-for="record in recordList"
          :key="record.id"
          class="record-item"
        >
          <div class="record-header">
            <span class="record-month">{{ record.paymentMonth }}</span>
            <span class="record-amount">￥{{ record.amount }}</span>
          </div>
          <div class="record-details">
            <span class="detail-text">房号：{{ record.roomNumber }}</span>
            <span class="detail-text">面积：{{ record.area }}㎡</span>
            <span class="detail-text">单价：{{ record.pricePerSqm }}元/㎡</span>
          </div>
          <div class="record-time">
            缴费时间：{{ formatDateTime(record.paymentTime) }}
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无缴费记录" />

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPaymentRecords } from '@/api/payment'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const recordList = ref([])
const total = ref(0)
const totalAmount = ref(0)

// 查询表单
const queryForm = reactive({
  startDate: '',
  endDate: '',
  pageNum: 1,
  pageSize: 10
})

// 初始化默认查询区间（最近3个月）
const initDefaultDate = () => {
  const now = new Date()
  const threeMonthsAgo = new Date()
  threeMonthsAgo.setMonth(now.getMonth() - 3)
  
  queryForm.endDate = formatDate(now)
  queryForm.startDate = formatDate(threeMonthsAgo)
}

// 格式化日期
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.replace('T', ' ')
}

// 查询缴费记录
const handleQuery = async () => {
  loading.value = true
  try {
    const params = {
      ...queryForm
    }
    
    const res = await getPaymentRecords(params)
    if (res.data) {
      recordList.value = res.data.list || []
      total.value = res.data.total || 0
      totalAmount.value = res.data.totalAmount || 0
    }
  } catch (error) {
    console.error('查询缴费记录失败:', error)
    ElMessage.error('查询缴费记录失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  initDefaultDate()
  queryForm.pageNum = 1
  queryForm.pageSize = 10
  handleQuery()
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  initDefaultDate()
  handleQuery()
})
</script>

<style scoped>
.payment-record-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  color: #303133;
  font-weight: 600;
  margin: 0;
}

.page-header .placeholder {
  width: 60px;
}

.query-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.query-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.query-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.stat-card {
  margin-bottom: 20px;
  border-radius: 8px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-content {
  text-align: center;
  padding: 10px 0;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-right: 10px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
}

.record-list-card {
  border-radius: 8px;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.record-item {
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
}

.record-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.1);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.record-month {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.record-amount {
  font-size: 20px;
  font-weight: 700;
  color: #f56c6c;
}

.record-details {
  display: flex;
  gap: 15px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.detail-text {
  font-size: 13px;
  color: #606266;
}

.record-time {
  font-size: 12px;
  color: #909399;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
