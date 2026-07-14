<template>
  <div class="profile-page">
    <div class="page-header">
      <h2>个人中心</h2>
    </div>

    <!-- 用户信息卡片 -->
    <el-card class="user-info-card" shadow="never">
      <template #header>
        <span>用户信息</span>
      </template>
      <div v-if="userInfo" class="info-list">
        <div class="info-item">
          <span class="label">房号：</span>
          <span class="value">{{ userInfo.roomNumber }}</span>
        </div>
        <div class="info-item">
          <span class="label">手机号：</span>
          <span class="value">{{ userInfo.phone }}</span>
        </div>
        <div v-if="residentInfo" class="info-item">
          <span class="label">姓名：</span>
          <span class="value">{{ residentInfo.name }}</span>
        </div>
        <div v-if="residentInfo" class="info-item">
          <span class="label">住房面积：</span>
          <span class="value">{{ residentInfo.area }} 平方米</span>
        </div>
        <div v-if="residentInfo" class="info-item">
          <span class="label">物业费单价：</span>
          <span class="value">{{ residentInfo.pricePerSqm }} 元/平方米</span>
        </div>
      </div>
    </el-card>

    <!-- 功能列表 -->
    <el-card class="function-card" shadow="never">
      <template #header>
        <span>功能菜单</span>
      </template>
      <div class="function-list">
        <div class="function-item" @click="showChangePassword = true">
          <el-icon><Lock /></el-icon>
          <span>修改密码</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="function-item" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>退出登录</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>
    </el-card>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="showChangePassword"
      title="修改密码"
      width="90%"
      :close-on-click-modal="false"
      style="max-width: 500px"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" disabled />
        </el-form-item>

        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="form.oldPassword"
            type="password"
            show-password
            placeholder="请输入原密码"
          />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="form.newPassword"
            type="password"
            show-password
            placeholder="请输入6-20位密码"
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            show-password
            placeholder="请再次输入新密码"
          />
        </el-form-item>

        <el-form-item label="验证码" prop="verifyCode">
          <el-input
            v-model="form.verifyCode"
            placeholder="请输入6位验证码"
            maxlength="6"
          >
            <template #append>
              <el-button
                :disabled="countdown > 0"
                @click="handleSendCode"
                style="width: 120px"
              >
                {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showChangePassword = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Lock, SwitchButton, ArrowRight } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getResidentInfo, sendVerifyCode, changePassword } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const showChangePassword = ref(false)
const submitLoading = ref(false)
const countdown = ref(0)

const userInfo = ref(null)
const residentInfo = ref(null)

// 表单数据
const form = reactive({
  phone: '',
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
  verifyCode: ''
})

// 确认密码验证
const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 表单验证规则
const rules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  verifyCode: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码为6位数字', trigger: 'blur' }
  ]
}

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    // 从Lstore中获取用户信息
    userInfo.value = userStore.userInfo
    
    // 设置手机号到表单
    form.phone = userInfo.value.phone || ''

    // 获取住户信息
    const userId = userStore.userInfo?.id
    if (userId) {
      const res = await getResidentInfo(userId)
      if (res.data) {
        residentInfo.value = res.data
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 发送验证码
const handleSendCode = async () => {
  if (!form.phone) {
    ElMessage.error('手机号为空')
    return
  }

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
    console.error('发送验证码失败:', error)
  }
}

// 提交修改
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      // 获取userId
      const userId = userStore.userInfo?.id
      if (!userId) {
        ElMessage.error('获取用户信息失败')
        return
      }
      
      await changePassword(userId, {
        phone: form.phone,
        oldPassword: form.oldPassword,
        newPassword: form.newPassword,
        verifyCode: form.verifyCode
      })
      
      ElMessage.success('密码修改成功，请重新登录')
      showChangePassword.value = false
      
      // 退出登录
      setTimeout(() => {
        handleLogout()
      }, 1500)
    } catch (error) {
      console.error('修改密码失败:', error)
      // 错误信息会由request拦截器统一处理
    } finally {
      submitLoading.value = false
    }
  })
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }).catch(() => {})
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.profile-page {
  max-width: 600px;
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

.user-info-card,
.function-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.info-list {
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

.label {
  color: #606266;
  font-size: 14px;
}

.value {
  color: #303133;
  font-size: 15px;
  font-weight: 500;
}

.function-list {
  display: flex;
  flex-direction: column;
}

.function-item {
  display: flex;
  align-items: center;
  padding: 15px 10px;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 1px solid #f0f0f0;
}

.function-item:last-child {
  border-bottom: none;
}

.function-item:hover {
  background: #f5f7fa;
}

.function-item .el-icon:first-child {
  margin-right: 12px;
  font-size: 18px;
  color: #409eff;
}

.function-item span {
  flex: 1;
  font-size: 15px;
  color: #303133;
}

.function-item .arrow {
  font-size: 14px;
  color: #909399;
}
</style>
