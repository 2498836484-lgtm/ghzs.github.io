<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">
        <el-icon><House /></el-icon>
        物业管理系统
      </h2>
      
      <el-tabs v-model="loginType" class="login-tabs">
        <el-tab-pane label="房号登录" name="room">
          <el-form ref="roomFormRef" :model="roomForm" :rules="rules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="roomForm.username"
                placeholder="请输入房号（如：A101）"
                prefix-icon="HomeFilled"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="roomForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                size="large"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="手机号登录" name="phone">
          <el-form ref="phoneFormRef" :model="phoneForm" :rules="rules" class="login-form">
            <el-form-item prop="username">
              <el-input
                v-model="phoneForm.username"
                placeholder="请输入手机号"
                prefix-icon="Iphone"
                size="large"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="phoneForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                size="large"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>

      <el-button
        type="primary"
        size="large"
        :loading="loading"
        class="login-btn"
        @click="handleLogin"
      >
        登 录
      </el-button>

      <div class="login-footer">
        <router-link to="/register">去注册</router-link>
        <router-link to="/admin-login">管理员登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { validateRoomNumber, validatePhone, validatePassword } from '@/utils/validators'

const router = useRouter()
const userStore = useUserStore()

const loginType = ref('room')
const loading = ref(false)

const roomForm = reactive({
  username: '',
  password: ''
})

const phoneForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ]
}

const roomFormRef = ref(null)
const phoneFormRef = ref(null)

const handleLogin = async () => {
  const formRef = loginType.value === 'room' ? roomFormRef.value : phoneFormRef.value
  const formData = loginType.value === 'room' ? roomForm : phoneForm

  await formRef.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const result = await userStore.login(formData, loginType.value)
        ElMessage.success('登录成功')
        
        // 根据用户入住状态跳转到不同页面
        // status: 0-未入住（跳转到申请页面），1-已入住（跳转到首页）
        if (result.userInfo && result.userInfo.status === 1) {
          router.push('/user/home')
        } else {
          router.push('/user/apply')
        }
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.login-container {
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.login-tabs {
  margin-bottom: 20px;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
  margin-top: 10px;
}

.login-footer {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  font-size: 14px;
  
  a {
    color: #409eff;
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
}
</style>
