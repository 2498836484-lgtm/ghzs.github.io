<template>
  <el-container class="user-layout">
    <el-header>
      <div class="header-content">
        <div class="logo">
          <el-icon><House /></el-icon>
          物业管理系统
        </div>
        <el-menu mode="horizontal" :default-active="activeMenu" router>
          <el-menu-item index="/user/home">首页</el-menu-item>
          <el-menu-item index="/user/apply">入住申请</el-menu-item>
          <el-menu-item index="/user/payment">缴费管理</el-menu-item>
          <el-menu-item index="/user/messages">
            <el-badge :value="unreadCount" :hidden="unreadCount === 0">
              消息中心
            </el-badge>
          </el-menu-item>
          <el-menu-item index="/user/profile">个人中心</el-menu-item>
        </el-menu>
        <div class="user-info">
          <el-dropdown @command="handleCommand">
            <span class="user-name">
              {{ userStore.userInfo?.username || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { useMessageStore } from '@/store/message'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const messageStore = useMessageStore()

const activeMenu = computed(() => route.path)
const unreadCount = computed(() => messageStore.unreadCount)

onMounted(() => {
  // 获取未读消息数量
  messageStore.fetchUnreadCount()
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
    })
  }
}
</script>

<style scoped lang="scss">
.user-layout {
  height: 100vh;
}

.el-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 0;
}

.header-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  margin-right: 50px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.el-menu {
  flex: 1;
  border: none;
}

.user-info {
  margin-left: 20px;
}

.user-name {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.el-main {
  background: #f5f7fa;
  padding: 20px;
}
</style>
