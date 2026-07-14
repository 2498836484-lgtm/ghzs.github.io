<template>
  <div class="home-container">
    <el-row :gutter="20">
      <!-- 欢迎卡片 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <template #header>
            <div class="card-header">
              <span>欢迎使用物业管理系统</span>
            </div>
          </template>
          <div class="welcome-content">
            <h3>您好，{{ userStore.userInfo?.username }}！</h3>
            <p v-if="residentInfo">
              您的房号：{{ residentInfo.roomNumber }} | 
              面积：{{ residentInfo.area }}㎡ | 
              单价：{{ residentInfo.pricePerSqm }}元/㎡
            </p>
            <p v-else class="tip-text">
              您还未入住，请前往 
              <router-link to="/user/apply">入住申请</router-link> 
              提交申请
            </p>
          </div>
        </el-card>
      </el-col>

      <!-- 快捷功能 -->
      <el-col :span="8">
        <el-card class="quick-card" shadow="hover" @click="router.push('/user/payment')">
          <div class="quick-item">
            <el-icon :size="40" color="#409eff"><Money /></el-icon>
            <div class="quick-text">
              <h4>缴费管理</h4>
              <p>查看和提交缴费记录</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="quick-card" shadow="hover" @click="router.push('/user/messages')">
          <div class="quick-item">
            <el-icon :size="40" color="#67c23a"><ChatDotRound /></el-icon>
            <div class="quick-text">
              <h4>消息中心</h4>
              <p>{{ unreadCount > 0 ? `${unreadCount}条未读消息` : '暂无未读消息' }}</p>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="quick-card" shadow="hover" @click="router.push('/user/profile')">
          <div class="quick-item">
            <el-icon :size="40" color="#e6a23c"><User /></el-icon>
            <div class="quick-text">
              <h4>个人中心</h4>
              <p>查看个人信息</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useMessageStore } from '@/store/message'

const router = useRouter()
const userStore = useUserStore()
const messageStore = useMessageStore()

const residentInfo = computed(() => userStore.residentInfo)
const unreadCount = computed(() => messageStore.unreadCount)

onMounted(async () => {
  if (userStore.userInfo?.userId) {
    await userStore.fetchResidentInfo(userStore.userInfo.userId)
  }
})
</script>

<style scoped lang="scss">
.home-container {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
  
  .card-header {
    font-size: 18px;
    font-weight: bold;
  }
  
  .welcome-content {
    h3 {
      margin-bottom: 15px;
      color: #303133;
    }
    
    p {
      margin: 10px 0;
      color: #606266;
      
      &.tip-text {
        color: #909399;
        
        a {
          color: #409eff;
          text-decoration: none;
          
          &:hover {
            text-decoration: underline;
          }
        }
      }
    }
  }
}

.quick-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-5px);
  }
  
  .quick-item {
    display: flex;
    align-items: center;
    gap: 15px;
    
    .quick-text {
      h4 {
        margin: 0 0 8px 0;
        font-size: 16px;
        color: #303133;
      }
      
      p {
        margin: 0;
        font-size: 14px;
        color: #909399;
      }
    }
  }
}
</style>
