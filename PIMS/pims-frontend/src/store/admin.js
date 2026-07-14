import { defineStore } from 'pinia'
import { ref } from 'vue'
import { adminLogin } from '@/api/admin'
import { setToken, setUserType, removeToken } from '@/utils/auth'
import { USER_TYPE } from '@/utils/constants'

export const useAdminStore = defineStore('admin', () => {
  const adminInfo = ref(null)

  // 管理员登录
  const login = async (data) => {
    try {
      const res = await adminLogin(data)
      
      if (res.data) {
        setToken(res.data.token)
        setUserType(USER_TYPE.ADMIN)
        adminInfo.value = {
          adminId: res.data.adminId,
          username: res.data.username,
          isSuperAdmin: res.data.isSuperAdmin
        }
        return res.data
      }
    } catch (error) {
      throw error
    }
  }

  // 登出
  const logout = () => {
    adminInfo.value = null
    removeToken()
  }

  return {
    adminInfo,
    login,
    logout
  }
})
