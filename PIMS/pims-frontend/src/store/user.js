import { defineStore } from 'pinia'
import { ref } from 'vue'
import { loginByRoom, loginByPhone, getResidentInfo } from '@/api/user'
import { setToken, setUserType, removeToken } from '@/utils/auth'
import { USER_TYPE } from '@/utils/constants'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref(null)
  const residentInfo = ref(null)

  // 登录
  const login = async (data, loginType = 'room') => {
    try {
      const loginFunc = loginType === 'room' ? loginByRoom : loginByPhone
      const res = await loginFunc(data)
      
      if (res.data) {
        setToken(res.data.token)
        setUserType(USER_TYPE.USER)
        // 保存完整的用户信息（包括status）
        userInfo.value = res.data.userInfo
        return res.data
      }
    } catch (error) {
      throw error
    }
  }

  // 登出
  const logout = () => {
    userInfo.value = null
    residentInfo.value = null
    removeToken()
  }

  // 获取住户信息
  const fetchResidentInfo = async (userId) => {
    try {
      const res = await getResidentInfo(userId)
      if (res.data) {
        residentInfo.value = res.data
        return res.data
      }
    } catch (error) {
      console.error('获取住户信息失败:', error)
    }
  }

  return {
    userInfo,
    residentInfo,
    login,
    logout,
    fetchResidentInfo
  }
})
