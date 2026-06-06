<!-- 技术栈：Vue 3, Element Plus, Axios, TypeScript -->
<template>
  <div class="address-page">
    <div class="header">
      <el-page-header @back="router.back" content="收货地址" />
    </div>

    <div v-loading="loading" class="content">
      <!-- 地址列表 -->
      <div v-if="addresses.length > 0" class="address-list">
        <div
            v-for="addr in addresses"
            :key="addr.id"
            class="address-card"
        >
          <div class="card-info" @click="selectDefault(addr)">
            <div class="contact-line">
              <span class="name">{{ addr.receiverName }}</span>
              <span class="phone">{{ addr.receiverPhone }}</span>
              <el-tag v-if="addr.isDefault === 1" size="small" type="success">默认</el-tag>
            </div>
            <div class="detail">
              {{ addr.province }}{{ addr.city }}{{ addr.district }} {{ addr.detail }}
            </div>
          </div>
          <div class="card-actions">
            <el-button type="primary" link size="small" @click="editAddress(addr)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button type="danger" link size="small" @click="deleteAddress(addr.id)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无收货地址" />

      <!-- 新增按钮 -->
      <div class="add-btn-wrapper">
        <el-button type="primary" round class="add-btn" @click="openDialog()">
          新增收货地址
        </el-button>
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑地址' : '新增地址'"
        width="90%"
        destroy-on-close
    >
      <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="80px"
      >
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="地区" prop="region">
          <el-cascader
              v-model="form.region"
              :options="regionOptions"
              placeholder="请选择省市区"
              style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" placeholder="街道/门牌号" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { addressApi } from '../../api/address'

const router = useRouter()

const loading = ref(false)
const addresses = ref<any[]>([])

// 表单
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentEditId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const submitting = ref(false)

const form = reactive({
  receiverName: '',
  receiverPhone: '',
  region: [] as string[],
  detail: '',
  isDefault: 0 as number
})

const validatePhone = (_rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  receiverName: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  receiverPhone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
  region: [{ required: true, message: '请选择地区', trigger: 'change' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// 省市区数据（简化版，仅演示）
const regionOptions = [
  {
    value: '江苏省',
    label: '江苏省',
    children: [
      {
        value: '镇江市',
        label: '镇江市',
        children: [
          { value: '京口区', label: '京口区' },
          { value: '润州区', label: '润州区' }
        ]
      },
      {
        value: '南京市',
        label: '南京市',
        children: [
          { value: '玄武区', label: '玄武区' },
          { value: '鼓楼区', label: '鼓楼区' }
        ]
      }
    ]
  }
]

// 加载地址列表
const fetchAddresses = async () => {
  loading.value = true
  try {
    const res = await addressApi.list()
    addresses.value = res.data || []
  } catch {
    // 使用模拟数据
    addresses.value = [
      {
        id: 1,
        receiverName: '张三',
        receiverPhone: '13800001111',
        province: '江苏省',
        city: '镇江市',
        district: '京口区',
        detail: '学府路301号',
        isDefault: 1
      }
    ]
  } finally {
    loading.value = false
  }
}

// 打开新增弹窗
const openDialog = (addr?: any) => {
  if (addr) {
    isEdit.value = true
    currentEditId.value = addr.id
    form.receiverName = addr.receiverName
    form.receiverPhone = addr.receiverPhone
    form.region = [addr.province, addr.city, addr.district]
    form.detail = addr.detail
    form.isDefault = addr.isDefault
  } else {
    isEdit.value = false
    currentEditId.value = null
    form.receiverName = ''
    form.receiverPhone = ''
    form.region = []
    form.detail = ''
    form.isDefault = 0
  }
  dialogVisible.value = true
}

// 编辑地址
const editAddress = (addr: any) => {
  openDialog(addr)
}

// 提交新增/编辑
const submitAddress = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    const data = {
      receiverName: form.receiverName,
      receiverPhone: form.receiverPhone,
      province: form.region[0] || '',
      city: form.region[1] || '',
      district: form.region[2] || '',
      detail: form.detail,
      isDefault: form.isDefault
    }
    try {
      if (isEdit.value && currentEditId.value) {
        await addressApi.update({ ...data, addressId: currentEditId.value })
        ElMessage.success('修改成功')
      } else {
        await addressApi.add(data)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      fetchAddresses()
    } catch {
      // 错误已提示
    } finally {
      submitting.value = false
    }
  })
}

// 设置默认地址
const selectDefault = async (addr: any) => {
  if (addr.isDefault === 1) return
  try {
    // 调用更新接口，将 isDefault 设为1
    await addressApi.update({
      addressId: addr.id,
      receiverName: addr.receiverName,
      receiverPhone: addr.receiverPhone,
      province: addr.province,
      city: addr.city,
      district: addr.district,
      detail: addr.detail,
      isDefault: 1
    })
    ElMessage.success('已设为默认地址')
    fetchAddresses()
  } catch {
    // 错误提示
  }
}

// 删除地址
const deleteAddress = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定删除该地址吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    await addressApi.remove(id)
    ElMessage.success('删除成功')
    fetchAddresses()
  } catch {
    // 错误提示
  }
}

onMounted(() => {
  fetchAddresses()
})
</script>

<style scoped>
.address-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 20px;
}
.header {
  background: #fff;
  padding: 10px 16px;
  margin-bottom: 8px;
}
.content {
  padding: 0 16px;
}
.address-card {
  background: #fff;
  border-radius: 8px;
  padding: 14px 16px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.card-info {
  flex: 1;
  cursor: pointer;
}
.contact-line {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}
.phone {
  color: #606266;
  font-size: 14px;
}
.detail {
  font-size: 13px;
  color: #909399;
}
.card-actions {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.add-btn-wrapper {
  margin-top: 20px;
  text-align: center;
}
.add-btn {
  width: 80%;
}
</style>