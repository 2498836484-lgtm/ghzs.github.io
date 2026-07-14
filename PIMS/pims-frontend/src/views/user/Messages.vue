<template>
  <div class="messages-page">
    <div class="page-header">
      <h2>系统消息</h2>
    </div>

    <!-- 消息列表 -->
    <div v-if="messageList.length > 0" class="message-list">
      <el-card
        v-for="message in messageList"
        :key="message.id"
        class="message-item"
        shadow="hover"
        :class="{ unread: message.isRead === 0 }"
      >
        <div class="message-header">
          <div class="message-title">
            <el-badge :is-dot="message.isRead === 0" type="danger">
              {{ message.title }}
            </el-badge>
          </div>
          <div class="message-time">{{ formatDateTime(message.createdTime) }}</div>
        </div>
        <div class="message-content">
          {{ message.content }}
        </div>
        <div class="message-type">
          <el-tag size="small" :type="getMessageTypeTag(message.messageType)">
            {{ getMessageTypeName(message.messageType) }}
          </el-tag>
        </div>
      </el-card>
    </div>

    <!-- 空状态 -->
    <el-empty v-else description="暂无消息" />

    <!-- 分页 -->
    <div v-if="total > 0" class="pagination">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getMessageList, markAllAsRead } from '@/api/message'
import { useMessageStore } from '@/store/message'
import { ElMessage } from 'element-plus'

const messageStore = useMessageStore()
const messageList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const loading = ref(false)

// 消息类型名称映射
const getMessageTypeName = (type) => {
  const typeMap = {
    1: '缴费提醒',
    2: '系统通知',
    3: '审核通知'
  }
  return typeMap[type] || '普通消息'
}

// 消息类型标签颜色
const getMessageTypeTag = (type) => {
  const tagMap = {
    1: 'warning',
    2: 'info',
    3: 'success'
  }
  return tagMap[type] || ''
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.replace('T', ' ')
}

// 查询消息列表
const handleQuery = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    
    const res = await getMessageList(params)
    if (res.data) {
      messageList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('查询消息列表失败:', error)
    ElMessage.error('查询消息列表失败')
  } finally {
    loading.value = false
  }
}

// 标记所有消息为已读
const markMessagesAsRead = async () => {
  try {
    await markAllAsRead()
    // 更新未读消息数量
    messageStore.setUnreadCount(0)
  } catch (error) {
    console.error('标记消息失败:', error)
  }
}

onMounted(() => {
  handleQuery()
  // 进入页面后标记所有消息为已读
  markMessagesAsRead()
})

onUnmounted(() => {
  // 离开页面后重新获取未读数量
  messageStore.fetchUnreadCount()
})
</script>

<style scoped>
.messages-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h2 {
  font-size: 24px;
  color: #303133;
  font-weight: 600;
}

.message-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}

.message-item {
  border-radius: 8px;
  transition: all 0.3s;
}

.message-item.unread {
  background: #f0f9ff;
  border-left: 3px solid #409eff;
}

.message-item:hover {
  transform: translateY(-2px);
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.message-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.message-time {
  font-size: 12px;
  color: #909399;
}

.message-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.8;
  margin-bottom: 12px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.message-type {
  text-align: right;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
