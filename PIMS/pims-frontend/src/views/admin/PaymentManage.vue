<template>
  <div class="payment-manage-page">
    <!-- 查询区域 -->
    <el-card shadow="never" class="query-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="房号">
          <el-input
            v-model="queryForm.roomNumber"
            placeholder="请输入房号"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="queryForm.startDate"
            type="date"
            placeholder="选择开始日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 150px"
          />
        </el-form-item>
        
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="queryForm.endDate"
            type="date"
            placeholder="选择结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 150px"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleQuery" :loading="loading">
            查询
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 统计卡片 -->
    <el-card shadow="never" class="stat-card" v-if="totalAmount > 0">
      <div class="stat-content">
        <div class="stat-item">
          <span class="stat-label">缴费总额：</span>
          <span class="stat-value">￥{{ totalAmount }}</span>
        </div>
        <div class="stat-item">
          <span class="stat-label">缴费笔数：</span>
          <span class="stat-value">{{ total }} 笔</span>
        </div>
      </div>
    </el-card>

    <!-- 缴费记录列表 -->
    <el-card shadow="never" style="margin-top: 20px">
      <el-table
        :data="paymentList"
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="roomNumber" label="房号" width="120" />
        <el-table-column prop="paymentMonth" label="缴费月份" width="120" />
        <el-table-column prop="area" label="面积（㎡）" width="120" />
        <el-table-column prop="pricePerSqm" label="单价（元/㎡）" width="130" />
        <el-table-column prop="amount" label="缴费金额（元）" width="130">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: 600">￥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentTime" label="缴费时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.paymentTime) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && paymentList.length === 0" description="暂无缴费记录" />

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
import { ElMessage } from 'element-plus'
import { getAllPaymentRecords } from '@/api/admin'

const loading = ref(false)
const paymentList = ref([])
const total = ref(0)
const totalAmount = ref(0)

// 查询表单
const queryForm = reactive({
  roomNumber: '',
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
    const params = { ...queryForm }
    
    const res = await getAllPaymentRecords(params)
    if (res.data) {
      paymentList.value = res.data.list || []
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
  queryForm.roomNumber = ''
  initDefaultDate()
  queryForm.pageNum = 1
  queryForm.pageSize = 10
  handleQuery()
}

onMounted(() => {
  initDefaultDate()
  handleQuery()
})
</script>

<style scoped>
.payment-manage-page {
  width: 100%;
}

.query-card {
  margin-bottom: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.stat-content {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  margin-left: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
