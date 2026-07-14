<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="register-title">
        <el-icon><UserFilled /></el-icon>
        用户注册
      </h2>
      
      <el-form ref="formRef" :model="form" :rules="rules" class="register-form" label-width="80px">
        <el-form-item label="手机号" prop="phone">
          <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            prefix-icon="Iphone"
          />
        </el-form-item>
        
        <el-form-item label="验证码" prop="verifyCode">
          <div style="display: flex; width: 100%;">
            <el-input
              v-model="form.verifyCode"
              placeholder="请输入验证码"
              prefix-icon="ChatDotRound"
              style="flex: 1;"
            />
            <el-button
              :disabled="countdown > 0"
              :loading="sendingCode"
              @click="handleSendCode"
              style="margin-left: 10px;"
            >
              {{ countdown > 0 ? `${countdown}s后重试` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="房号" prop="roomNumber">
          <el-input
            v-model="form.roomNumber"
            placeholder="请输入房号（如：A101）"
            prefix-icon="HomeFilled"
          />
        </el-form-item>
        
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码（至少6位）"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleRegister"
            style="width: 100%;"
          >
            注 册
          </el-button>
        </el-form-item>
      </el-form>

      <div class="register-footer">
        <router-link to="/login">已有账号？去登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register, sendVerifyCode } from '@/api/user'
import { validatePhone, validateRoomNumber, validatePassword, validateVerifyCode } from '@/utils/validators'

const router = useRouter()

const form = reactive({
  phone: '',
  verifyCode: '',
  roomNumber: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)
const sendingCode = ref(false)
const countdown = ref(0)

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  phone: [
    { required: true, validator: validatePhone, trigger: 'blur' }
  ],
  verifyCode: [
    { required: true, validator: validateVerifyCode, trigger: 'blur' }
  ],
  roomNumber: [
    { required: true, validator: validateRoomNumber, trigger: 'blur' }
  ],
  password: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const formRef = ref(null)

// 发送验证码
const handleSendCode = async () => {
  if (!form.phone) {
    ElMessage.warning('请先输入手机号')
    return
  }
  
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.error('请输入正确的手机号')
    return
  }
  
  sendingCode.value = true
  try {
    await sendVerifyCode(form.phone)
    ElMessage.success('验证码已发送，请查看控制台日志')
    
    // 开始倒计时
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    ElMessage.error(error.message || '发送验证码失败')
  } finally {
    sendingCode.value = false
  }
}

// 注册
const handleRegister = async () => {
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await register({
          phone: form.phone,
          verifyCode: form.verifyCode,
          roomNumber: form.roomNumber,
          password: form.password
        })
        ElMessage.success('注册成功！即将跳转到登录页...')
        setTimeout(() => {
          router.push('/login')
        }, 1500)
      } catch (error) {
        ElMessage.error(error.message || '注册失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.register-container {
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-box {
  width: 500px;
  padding: 40px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.register-form {
  margin-top: 20px;
}

.register-footer {
  text-align: center;
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
