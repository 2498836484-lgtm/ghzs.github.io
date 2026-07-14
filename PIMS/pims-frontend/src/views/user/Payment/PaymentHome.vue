<template>
  <div class="payment-home">
    <!-- 住户信息卡片 -->
    <el-card class="resident-info-card" shadow="never">
      <template #header>
        <span>住户信息</span>
      </template>
      <div v-if="residentInfo" class="info-content">
        <div class="info-item">
          <span class="label">房号：</span>
          <span class="value">{{ residentInfo.roomNumber }}</span>
        </div>
        <div class="info-item">
          <span class="label">住房面积：</span>
          <span class="value">{{ residentInfo.area }} 平方米</span>
        </div>
        <div class="info-item">
          <span class="label">物业费单价：</span>
          <span class="value">{{ residentInfo.pricePerSqm }} 元/平方米</span>
        </div>
        <div class="info-item highlight">
          <span class="label">本月应缴金额：</span>
          <span class="value amount">￥{{ monthlyPayment }}</span>
        </div>
      </div>
      <div v-else class="empty-info">
        <el-empty description="未获取到住户信息" />
      </div>
    </el-card>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button
        type="primary"
        size="large"
        @click="goToPayNow"
        :disabled="!residentInfo"
        class="action-btn"
      >
        <el-icon><CreditCard /></el-icon>
        立即缴费
      </el-button>
      
      <el-button
        size="large"
        @click="goToPaymentRecord"
        class="action-btn"
      >
        <el-icon><Document /></el-icon>
        缴费记录查询
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CreditCard, Document } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getResidentInfo } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const residentInfo = ref(null)

// 计算本月应缴金额
const monthlyPayment = computed(() => {
  if (!residentInfo.value) return 0
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

// 跳转到立即缴费页面
const goToPayNow = () => {
  router.push({
    path: '/user/payment/pay-now',
    query: {
      amount: monthlyPayment.value
    }
  })
}

// 跳转到缴费记录页面
const goToPaymentRecord = () => {
  router.push('/user/payment/record')
}

onMounted(() => {
  fetchResidentInfo()
})
</script>

<style scoped>
.payment-home {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.resident-info-card {
  margin-bottom: 30px;
  border-radius: 8px;
}

.info-content {
  padding: 10px 0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item.highlight {
  margin-top: 10px;
  padding-top: 20px;
  border-top: 2px dashed #409eff;
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

.amount {
  color: #f56c6c;
  font-size: 24px;
  font-weight: 600;
}

.empty-info {
  padding: 40px 0;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.action-btn {
  width: 100%;
  height: 60px;
  font-size: 16px;
  font-weight: 500;
}

.action-btn :deep(.el-icon) {
  margin-right: 8px;
  font-size: 20px;
}
</style>
