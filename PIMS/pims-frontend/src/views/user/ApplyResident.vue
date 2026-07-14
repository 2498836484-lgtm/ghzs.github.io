<template>
  <div class="apply-resident-page">
    <div class="page-header">
      <h2>入住申请</h2>
    </div>

    <!-- 未申请状态 -->
    <el-card v-if="!auditStatus" class="apply-form-card" shadow="never">
      <template #header>
        <span>填写入住信息</span>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="apply-form"
      >
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入真实姓名"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="身份证号" prop="idCard">
          <el-input
            v-model="form.idCard"
            placeholder="请输入18位身份证号"
            maxlength="18"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="住房面积" prop="area">
          <el-input
            v-model.number="form.area"
            placeholder="请输入住房面积（平方米）"
            type="number"
            step="0.01"
          >
            <template #append>平方米</template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="submitLoading"
            @click="handleSubmit"
            size="large"
            style="width: 100%"
          >
            提交申请
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 审核中状态 -->
    <el-card v-else-if="auditStatus.status === 0" class="status-card" shadow="never">
      <el-result icon="info" title="审核中" sub-title="您的入住申请正在审核中，请耐心等待">
        <template #extra>
          <div class="audit-info">
            <p><strong>申请信息：</strong></p>
            <p>姓名：{{ auditStatus.name }}</p>
            <p>身份证号：{{ auditStatus.idCard }}</p>
            <p>住房面积：{{ auditStatus.area }} 平方米</p>
            <p>申请时间：{{ auditStatus.applyTime }}</p>
          </div>
        </template>
      </el-result>
    </el-card>

    <!-- 审核拒绝状态 -->
    <el-card v-else-if="auditStatus.status === 2" class="status-card" shadow="never">
      <el-result icon="error" title="审核未通过" sub-title="您的入住申请未通过审核，请重新申请">
        <template #extra>
          <el-button type="primary" @click="handleReApply">重新申请</el-button>
        </template>
      </el-result>
    </el-card>

    <!-- 审核通过状态（自动跳转） -->
    <el-card v-else-if="auditStatus.status === 1" class="status-card" shadow="never">
      <el-result icon="success" title="审核通过" sub-title="您的入住申请已通过，正在跳转...">
      </el-result>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { applyResident, getAuditStatus } from '@/api/user'
import { idCardValidator } from '@/utils/validators'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const submitLoading = ref(false)
const auditStatus = ref(null)

// 表单数据
const form = reactive({
  name: '',
  idCard: '',
  area: null
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度为2-20个字符', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { validator: idCardValidator, trigger: 'blur' }
  ],
  area: [
    { required: true, message: '请输入住房面积', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '住房面积必须大于0', trigger: 'blur' }
  ]
}

// 获取审核状态
const fetchAuditStatus = async () => {
  try {
    const userId = userStore.userInfo?.id
    if (!userId) {
      console.error('未获取到用户ID')
      return
    }
    
    const res = await getAuditStatus(userId)
    if (res.data) {
      auditStatus.value = res.data
      
      // 如果审核通过，更新用户状态并跳转到用户主页
      if (res.data.status === 1) {
        // 更新userStore中的状态
        if (userStore.userInfo) {
          userStore.userInfo.status = 1
        }
        
        setTimeout(() => {
          router.push('/user/home')
        }, 1500)
      }
    }
  } catch (error) {
    console.error('获取审核状态失败:', error)
  }
}

// 提交申请
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      const userId = userStore.userInfo?.id
      if (!userId) {
        ElMessage.error('未获取到用户信息')
        return
      }
      
      await applyResident(userId, form)
      ElMessage.success('申请提交成功，请等待审核')
      
      // 重新获取审核状态
      await fetchAuditStatus()
    } catch (error) {
      console.error('提交申请失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 重新申请
const handleReApply = () => {
  auditStatus.value = null
  form.name = ''
  form.idCard = ''
  form.area = null
}

// 页面加载时获取审核状态
onMounted(() => {
  fetchAuditStatus()
})
</script>

<style scoped>
.apply-resident-page {
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

.apply-form-card,
.status-card {
  border-radius: 8px;
}

.apply-form {
  padding: 20px 0;
}

.audit-info {
  text-align: left;
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin-top: 20px;
}

.audit-info p {
  margin: 8px 0;
  color: #606266;
  line-height: 1.8;
}

.audit-info strong {
  color: #303133;
}
</style>
