<template>
  <div class="menu-manage">
    <div class="header">
      <el-page-header @back="router.back" content="菜品管理" />
      <el-button type="primary" @click="openAddDialog">添加菜品</el-button>
    </div>
    <el-table :data="menuList" style="width: 100%" v-loading="loading">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="price" label="价格" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button link @click="openEditDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="deleteMenu(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜品' : '添加菜品'" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input v-model.number="form.price" type="number" />
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="form.image" placeholder="图片URL" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import request from '../../api/request'

const router = useRouter()
const loading = ref(false)
const menuList = ref<any[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentEditId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive({
  name: '',
  price: 0,
  image: '',
  description: '',
  status: 1
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

// 加载菜品列表
const fetchMenu = async () => {
  loading.value = true
  try {
    const res = await request.get('/merchant/menu/list')
    menuList.value = res.data || []
  } catch (e) {
    // error
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  currentEditId.value = null
  form.name = ''
  form.price = 0
  form.image = ''
  form.description = ''
  form.status = 1
  dialogVisible.value = true
}

const openEditDialog = (row: any) => {
  isEdit.value = true
  currentEditId.value = row.id
  form.name = row.name
  form.price = row.price
  form.image = row.image || ''
  form.description = row.description || ''
  form.status = row.status
  dialogVisible.value = true
}

const deleteMenu = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定删除该菜品吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    await request.post('/merchant/menu/delete', null, { params: { itemId: id } })
    ElMessage.success('删除成功')
    fetchMenu()
  } catch (e) {
    // error
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    const params: any = {
      name: form.name,
      price: form.price,
      image: form.image,
      description: form.description,
      status: form.status
    }
    try {
      if (isEdit.value && currentEditId.value) {
        await request.post('/merchant/menu/update', null, {
          params: { ...params, itemId: currentEditId.value }
        })
        ElMessage.success('修改成功')
      } else {
        await request.post('/merchant/menu/add', null, { params })
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      fetchMenu()
    } catch (e) {
      // error
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  fetchMenu()
})
</script>

<style scoped>
.menu-manage {
  padding: 16px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
</style>