<template>
  <div class="audit-manage-page">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>待审核列表</span>
          <el-button type="primary" :icon="Refresh" @click="fetchAuditList" circle />
        </div>
      </template>

      <!-- 审核列表 -->
      <el-table
        :data="auditList"
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="roomNumber" label="房号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="area" label="住房面积（㎡）" width="140" />
        <el-table-column prop="createdTime" label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button
              type="success"
              size="small"
              @click="handleApprove(row)"
            >
              同意
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleReject(row)"
            >
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && auditList.length === 0" description="暂无待审核的申请" />

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="fetchAuditList"
          @current-change="fetchAuditList"
        />
      </div>
    </el-card>

    <!-- 同意审核对话框 -->
    <el-dialog
      v-model="showApproveDialog"
      title="审核通过"
      width="90%"
      style="max-width: 500px"
    >
      <el-form :model="approveForm" label-width="120px">
        <el-form-item label="姓名">
          <el-input v-model="currentRecord.name" disabled />
        </el-form-item>
        <el-form-item label="房号">
          <el-input v-model="currentRecord.roomNumber" disabled />
        </el-form-item>
        <el-form-item label="住房面积">
          <el-input v-model="currentRecord.area" disabled>
            <template #append>平方米</template>
          </el-input>
        </el-form-item>
        <el-form-item label="物业费单价" required>
          <el-input
            v-model.number="approveForm.pricePerSqm"
            type="number"
            placeholder="请输入物业费单价"
            step="0.1"
          >
            <template #append>元/㎡</template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showApproveDialog = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="confirmApprove">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getPendingAuditList, processAudit } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const auditList = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const showApproveDialog = ref(false)
const currentRecord = ref({})

// 同意表单
const approveForm = reactive({
  pricePerSqm: 2.0
})

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.replace('T', ' ')
}

// 获取审核列表
const fetchAuditList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    
    const res = await getPendingAuditList(params)
    if (res.data) {
      auditList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('获取审核列表失败:', error)
    ElMessage.error('获取审核列表失败')
  } finally {
    loading.value = false
  }
}

// 处理同意
const handleApprove = (record) => {
  currentRecord.value = record
  approveForm.pricePerSqm = 2.0 // 默认单价
  showApproveDialog.value = true
}

// 确认同意
const confirmApprove = async () => {
  if (!approveForm.pricePerSqm || approveForm.pricePerSqm <= 0) {
    ElMessage.error('请输入正确的物业费单价')
    return
  }

  submitLoading.value = true
  try {
    await processAudit({
      auditId: currentRecord.value.id,
      status: 1, // 1-同意
      pricePerSqm: approveForm.pricePerSqm
    })
    
    ElMessage.success('审核通过')
    showApproveDialog.value = false
    
    // 刷新列表
    fetchAuditList()
  } catch (error) {
    console.error('审核失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 处理拒绝
const handleReject = (record) => {
  ElMessageBox.confirm(
    `确认拒绝【${record.name}】的入住申请吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await processAudit({
        auditId: record.id,
        status: 2 // 2-拒绝
      })
      
      ElMessage.success('已拒绝')
      
      // 刷新列表
      fetchAuditList()
    } catch (error) {
      console.error('审核失败:', error)
    }
  }).catch(() => {})
}

onMounted(() => {
  fetchAuditList()
})
</script>

<style scoped>
.audit-manage-page {
  width: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
