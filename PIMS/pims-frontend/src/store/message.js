import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUnreadCount, getMessageList, markAsRead, markAllAsRead } from '@/api/message'

export const useMessageStore = defineStore('message', () => {
  const unreadCount = ref(0)
  const messageList = ref([])

  // 获取未读消息数量
  const fetchUnreadCount = async () => {
    try {
      const res = await getUnreadCount()
      if (res.data !== undefined) {
        unreadCount.value = res.data
      }
    } catch (error) {
      console.error('获取未读消息数量失败:', error)
    }
  }

  // 获取消息列表
  const fetchMessageList = async (params) => {
    try {
      const res = await getMessageList(params)
      if (res.data) {
        messageList.value = res.data.records || []
        return res.data
      }
    } catch (error) {
      console.error('获取消息列表失败:', error)
    }
  }

  // 标记消息为已读
  const readMessage = async (id) => {
    try {
      await markAsRead(id)
      await fetchUnreadCount()
    } catch (error) {
      console.error('标记消息失败:', error)
    }
  }

  // 标记所有消息为已读
  const readAllMessages = async () => {
    try {
      await markAllAsRead()
      unreadCount.value = 0
    } catch (error) {
      console.error('标记所有消息失败:', error)
    }
  }

  return {
    unreadCount,
    messageList,
    fetchUnreadCount,
    fetchMessageList,
    readMessage,
    readAllMessages
  }
})
