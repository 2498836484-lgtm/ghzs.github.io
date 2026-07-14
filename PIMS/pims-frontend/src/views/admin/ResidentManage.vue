<template>
  <div class="resident-manage-page">
    <!-- 查询区域 -->
    <el-card shadow="never" class="query-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="房号">
          <el-input
            v-model="queryForm.roomNumber"
            placeholder="请输入房号"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        
        <el-form-item label="姓名">
          <el-input
            v-model="queryForm.name"
            placeholder="请输入姓名"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        
        <el-form-item label="手机号">
          <el-input
            v-model="queryForm.phone"
            placeholder="请输入手机号"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        
        <el-form-item label="入住状态">
          <el-select
            v-model="queryForm.status"
            placeholder="全部"
            clearable
            style="width: 120px"
          >
            <el-option label="已入住" :value="1" />
            <el-option label="未入住" :value="0" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleQuery" :loading="loading">
            查询
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 住户列表 -->
    <el-card shadow="never" style="margin-top: 20px">
      <el-table
        :data="residentList"
        v-loading="loading"
        style="width: 100%"
        stripe
      >
        <el-table-column prop="roomNumber" label="房号" width="100" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="area" label="住房面积（㎡）" width="130" />
        <el-table-column prop="pricePerSqm" label="物业费单价（元/㎡）" width="150" />
        <el-table-column prop="status" label="入住状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已入住' : '未入住' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="注册时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.registerTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="row.status === 1"
              type="danger"
              size="small"
              @click="handleCheckout(row)"
            >
              退房
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 空状态 -->
      <el-empty v-if="!loading && residentList.length === 0" description="暂无住户信息" />

      <!-- 分页 -->
      <div v-if="total > 0" class="pagination">
        <el-pagination
          v-model:current-page="queryForm.pageNum"
          v-model:page-size="queryForm.pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </el-card>

    <!-- 编辑对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑住户信息"
      width="90%"
      style="max-width: 500px"
    >
      <el-form :model="editForm" label-width="120px">
        <el-form-item label="姓名">
          <el-input v-model="currentRecord.name" disabled />
        </el-form-item>
        <el-form-item label="房号">
          <el-input v-model="currentRecord.roomNumber" disabled />
        </el-form-item>
        <el-form-item label="物业费单价">
          <el-input
            v-model.number="editForm.pricePerSqm"
            type="number"
            placeholder="请输入物业费单价"
            step="0.1"
          >
            <template #append>元/㎡</template>
          </el-input>
        </el-form-item>
        <el-form-item label="入住状态">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="已入住" :value="1" />
            <el-option label="未入住" :value="0" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showEditDialog = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="confirmEdit">
            保存
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getResidentList, updateResident, checkoutResident } from '@/api/admin'

const loading = ref(false)
const submitLoading = ref(false)
const residentList = ref([])
const total = ref(0)
const showEditDialog = ref(false)
const currentRecord = ref({})

// 查询表单
const queryForm = reactive({
  roomNumber: '',
  name: '',
  phone: '',
  status: undefined,
  pageNum: 1,
  pageSize: 10
})

// 编辑表单
const editForm = reactive({
  pricePerSqm: 0,
  status: 1
})

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  return dateTimeStr.replace('T', ' ')
}

// 查询住户列表
const handleQuery = async () => {
  loading.value = true
  try {
    const params = { ...queryForm }
    
    const res = await getResidentList(params)
    if (res.data) {
      residentList.value = res.data.list || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    console.error('查询住户列表失败:', error)
    ElMessage.error('查询住户列表失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  queryForm.roomNumber = ''
  queryForm.name = ''
  queryForm.phone = ''
  queryForm.status = undefined
  queryForm.pageNum = 1
  queryForm.pageSize = 10
  handleQuery()
}

// 处理编辑
const handleEdit = (record) => {
  currentRecord.value = record
  editForm.pricePerSqm = record.pricePerSqm
  editForm.status = record.status
  showEditDialog.value = true
}

// 确认编辑
const confirmEdit = async () => {
  submitLoading.value = true
  try {
    await updateResident({
      residentId: currentRecord.value.id,
      pricePerSqm: editForm.pricePerSqm,
      status: editForm.status
    })
    
    ElMessage.success('修改成功')
    showEditDialog.value = false
    
    // 刷新列表
    handleQuery()
  } catch (error) {
    console.error('修改失败:', error)
  } finally {
    submitLoading.value = false
  }
}

// 处理退房
const handleCheckout = (record) => {
  ElMessageBox.confirm(
    `确认为【${record.name}】办理退房吗？退房前会检查物业费是否结清。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await checkoutResident(record.id)
      ElMessage.success('退房成功')
      
      // 刷新列表
      handleQuery()
    } catch (error) {
      console.error('退房失败:', error)
    }
  }).catch(() => {})
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.resident-manage-page {
  width: 100%;
}

.query-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
