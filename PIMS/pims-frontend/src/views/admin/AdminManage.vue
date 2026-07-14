<template>
  <div class="admin-manage-page">
    <!-- 操作区域（仅超级管理员可见） -->
    <el-card v-if="isSuperAdmin" shadow="never" class="action-card">
      <el-button type="primary" @click="showCreateDialog = true">
        <el-icon><Plus /></el-icon>
        创建管理员
      </el-button>
    </el-card>

    <!-- 管理员列表 -->
    <el-card shadow="never" :style="isSuperAdmin ? 'margin-top: 20px' : ''">
      <template #header>
        <div class="card-header">
          <span>管理员列表</span>
          <el-button type="primary" :icon="Refresh" @click="fetchAdminList" circle />
        </div>
      </template>

      <el-table
        :data="adminList"
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="username" label="用户名" width="200" />
        <el-table-column prop="isSuperAdmin" label="角色" width="150">
          <template #default="{ row }">
            <el-tag :type="row.isSuperAdmin === 1 ? 'danger' : 'primary'" size="small">
              {{ row.isSuperAdmin === 1 ? '超级管理员' : '普通管理员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="canModifyPassword(row)"
              type="primary"
              size="small"
              @click="handleChangePassword(row)"
            >
              修改密码
            </el-button>
            <span v-else style="color: #909399; font-size: 12px">无权限</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && adminList.length === 0" description="暂无管理员信息" />
    </el-card>

    <!-- 创建管理员对话框 -->
    <el-dialog
      v-model="showCreateDialog"
      title="创建管理员"
      width="90%"
      style="max-width: 500px"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="createForm.username"
            placeholder="请输入用户名"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="初始密码" prop="password">
          <el-input
            v-model="createForm.password"
            type="password"
            placeholder="请输入初始密码"
            show-password
            maxlength="20"
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="createForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            maxlength="20"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCreateDialog = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="confirmCreate">
            创建
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="showPasswordDialog"
      title="修改密码"
      width="90%"
      style="max-width: 500px"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="100px"
      >
        <el-form-item label="用户名">
          <el-input v-model="currentAdmin.username" disabled />
        </el-form-item>
        
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
            maxlength="20"
          />
        </el-form-item>
        
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="passwordForm.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
            maxlength="20"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showPasswordDialog = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="confirmChangePassword">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { useAdminStore } from '@/store/admin'
import { getAdminList, createAdmin, changeAdminPassword } from '@/api/admin'

const adminStore = useAdminStore()
const loading = ref(false)
const submitLoading = ref(false)
const adminList = ref([])
const showCreateDialog = ref(false)
const showPasswordDialog = ref(false)
const currentAdmin = ref({})
const createFormRef = ref()
const passwordFormRef = ref()

// 当前管理员信息
const adminInfo = computed(() => adminStore.adminInfo)

// 是否为超级管理员
const isSuperAdmin = computed(() => adminInfo.value?.isSuperAdmin === 1)

// 创建表单
const createForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

// 密码表单
const passwordForm = reactive({
  newPassword: '',
  confirmPassword: ''
})

// 确认密码验证器（创建）
const validateCreateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== createForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 确认密码验证器（修改）
const validatePasswordConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 创建表单验证规则
const createRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateCreateConfirmPassword, trigger: 'blur' }
  ]
}

// 修改密码验证规则
const passwordRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validatePasswordConfirmPassword, trigger: 'blur' }
  ]
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.replace('T', ' ')
}

// 判断是否可以修改密码
const canModifyPassword = (admin) => {
  // 超级管理员可以修改所有人的密码
  if (isSuperAdmin.value) return true
  
  // 普通管理员只能修改自己的密码
  return admin.id === adminInfo.value?.id
}

// 获取管理员列表
const fetchAdminList = async () => {
  loading.value = true
  try {
    const res = await getAdminList()
    if (res.data) {
      adminList.value = res.data
    }
  } catch (error) {
    console.error('获取管理员列表失败:', error)
    ElMessage.error('获取管理员列表失败')
  } finally {
    loading.value = false
  }
}

// 确认创建
const confirmCreate = async () => {
  if (!createFormRef.value) return
  
  await createFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      await createAdmin({
        username: createForm.username,
        password: createForm.password
      })
      
      ElMessage.success('管理员创建成功')
      showCreateDialog.value = false
      
      // 重置表单
      createForm.username = ''
      createForm.password = ''
      createForm.confirmPassword = ''
      
      // 刷新列表
      fetchAdminList()
    } catch (error) {
      console.error('创建管理员失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 处理修改密码
const handleChangePassword = (admin) => {
  currentAdmin.value = admin
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  showPasswordDialog.value = true
}

// 确认修改密码
const confirmChangePassword = async () => {
  if (!passwordFormRef.value) return
  
  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      await changeAdminPassword({
        adminId: currentAdmin.value.id,
        newPassword: passwordForm.newPassword
      })
      
      ElMessage.success('密码修改成功')
      showPasswordDialog.value = false
    } catch (error) {
      console.error('修改密码失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => {
  fetchAdminList()
})
</script>

<style scoped>
.admin-manage-page {
  width: 100%;
}

.action-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
