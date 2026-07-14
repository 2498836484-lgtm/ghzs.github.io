<template>
  <div class="pay-now-page">
    <div class="page-header">
      <el-button @click="goBack" icon="ArrowLeft" text>返回</el-button>
      <h2>立即缴费</h2>
      <div class="placeholder"></div>
    </div>

    <el-card class="payment-detail-card" shadow="never">
      <template #header>
        <span>缴费信息</span>
      </template>
      
      <div v-if="residentInfo" class="detail-content">
        <div class="detail-item">
          <span class="label">房号：</span>
          <span class="value">{{ residentInfo.roomNumber }}</span>
        </div>
        <div class="detail-item">
          <span class="label">住房面积：</span>
          <span class="value">{{ residentInfo.area }} 平方米</span>
        </div>
        <div class="detail-item">
          <span class="label">物业费单价：</span>
          <span class="value">{{ residentInfo.pricePerSqm }} 元/平方米</span>
        </div>
        <div class="detail-item">
          <span class="label">缴费月份：</span>
          <span class="value">{{ currentMonth }}</span>
        </div>
        
        <div class="amount-section">
          <div class="amount-label">应缴金额</div>
          <div class="amount-value">￥{{ paymentAmount }}</div>
          <div class="amount-tip">（面积 × 单价）</div>
        </div>
      </div>

      <div class="action-section">
        <el-button
          type="primary"
          size="large"
          :loading="paying"
          @click="handlePay"
          class="pay-button"
        >
          确认缴费
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getResidentInfo } from '@/api/user'
import { submitPayment } from '@/api/payment'

const router = useRouter()
const userStore = useUserStore()
const residentInfo = ref(null)
const paying = ref(false)

// 当前月份
const currentMonth = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  return `${year}-${month}`
})

// 应缴金额
const paymentAmount = computed(() => {
  if (!residentInfo.value) return '0.00'
  return (residentInfo.value.area * residentInfo.value.pricePerSqm).toFixed(2)
})

// 获取住户信息
const fetchResidentInfo = async () => {
  try {
    const userId = userStore.userInfo?.id
    if (!userId) {
      ElMessage.error('未获取到用户信息')
      return
    }
    const res = await getResidentInfo(userId)
    if (res.data) {
      residentInfo.value = res.data
    }
  } catch (error) {
    console.error('获取住户信息失败:', error)
    ElMessage.error('获取住户信息失败')
  }
}

// 处理缴费
const handlePay = async () => {
  if (!residentInfo.value) {
    ElMessage.error('未获取到住户信息')
    return
  }

  paying.value = true
  try {
    const paymentData = {
      paymentMonth: currentMonth.value,
      amount: parseFloat(paymentAmount.value),
      roomNumber: residentInfo.value.roomNumber,
      area: residentInfo.value.area,
      pricePerSqm: residentInfo.value.pricePerSqm
    }

    await submitPayment(paymentData)
    ElMessage.success('缴费成功！')

    // 2秒后返回缴费首页
    setTimeout(() => {
      router.push('/user/payment')
    }, 2000)
  } catch (error) {
    console.error('缴费失败:', error)
  } finally {
    paying.value = false
  }
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  fetchResidentInfo()
})
</script>

<style scoped>
.pay-now-page {
  max-width: 600px;
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

.payment-detail-card {
  border-radius: 8px;
}

.detail-content {
  padding: 10px 0;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-of-type {
  border-bottom: none;
}

.label {
  color: #606266;
  font-size: 14px;
}

.value {
  color: #303133;
  font-size: 15px;
  font-weight: 500;
}

.amount-section {
  text-align: center;
  padding: 30px 20px;
  margin-top: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.amount-label {
  font-size: 14px;
  opacity: 0.9;
  margin-bottom: 10px;
}

.amount-value {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 5px;
}

.amount-tip {
  font-size: 12px;
  opacity: 0.8;
}

.action-section {
  padding: 20px 0 0;
}

.pay-button {
  width: 100%;
  height: 50px;
  font-size: 18px;
  font-weight: 600;
}
</style>
