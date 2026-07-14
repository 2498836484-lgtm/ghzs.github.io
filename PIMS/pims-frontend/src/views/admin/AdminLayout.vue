<template>
  <el-container class="admin-layout">
    <!-- 左侧边栏 -->
    <el-aside width="200px" class="sidebar">
      <div class="logo-area">
        <h2>物业管理系统</h2>
        <p>管理员后台</p>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
      >
        <el-menu-item index="/admin/audit">
          <el-icon><Check /></el-icon>
          <span>入住审核</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/resident">
          <el-icon><UserFilled /></el-icon>
          <span>住户信息管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/payment">
          <el-icon><Wallet /></el-icon>
          <span>缴费记录查询</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/admin-manage">
          <el-icon><Setting /></el-icon>
          <span>管理员信息</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span>{{ adminInfo?.username || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Check,
  UserFilled,
  Wallet,
  Setting,
  User,
  ArrowDown,
  SwitchButton
} from '@element-plus/icons-vue'
import { useAdminStore } from '@/store/admin'

const route = useRoute()
const router = useRouter()
const adminStore = useAdminStore()

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 管理员信息
const adminInfo = computed(() => adminStore.adminInfo)

// 页面标题映射
const pageTitleMap = {
  '/admin/audit': '入住审核',
  '/admin/resident': '住户信息管理',
  '/admin/payment': '缴费记录查询',
  '/admin/admin-manage': '管理员信息'
}

// 当前页面标题
const pageTitle = computed(() => {
  return pageTitleMap[route.path] || '管理员后台'
})

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    handleLogout()
  }
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    adminStore.logout()
    ElMessage.success('已退出登录')
    router.push('/admin-login')
  }).catch(() => {})
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.sidebar {
  background: #304156;
  color: white;
  overflow-y: auto;
}

.logo-area {
  padding: 20px;
  text-align: center;
  background: #263445;
  border-bottom: 1px solid #1f2d3d;
}

.logo-area h2 {
  margin: 0 0 8px;
  font-size: 18px;
  color: white;
  font-weight: 600;
}

.logo-area p {
  margin: 0;
  font-size: 12px;
  color: #909399;
}

.sidebar-menu {
  border: none;
  background: #304156;
}

.sidebar-menu :deep(.el-menu-item) {
  color: #bfcbd9;
  border-left: 3px solid transparent;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: #263445;
  color: white;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: #263445;
  color: #409eff;
  border-left-color: #409eff;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}

.header-left .page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.user-info:hover {
  background: #f5f7fa;
}

.user-info .el-icon {
  font-size: 16px;
}

.main-content {
  background: #f5f7fa;
  padding: 20px;
  overflow-y: auto;
}
</style>
