<template>
  <div class="register-container">
    <div class="logo">
      <h1>商家入驻</h1>
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item prop="name">
        <el-input v-model="form.name" placeholder="店铺名称" />
      </el-form-item>
      <el-form-item prop="phone">
        <el-input v-model="form.phone" placeholder="手机号" />
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="form.password" type="password" placeholder="密码" show-password />
      </el-form-item>
      <el-form-item prop="address">
        <el-input v-model="form.address" placeholder="店铺地址（选填）" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" round :loading="loading" class="register-btn" @click="handleRegister">
          立即入驻
        </el-button>
      </el-form-item>
    </el-form>
    <div class="login-link">
      已有店铺？<router-link to="/merchant/login">立即登录</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useMerchantStore } from '../../stores/merchant'

const router = useRouter()
const merchantStore = useMerchantStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  name: '',
  phone: '',
  password: '',
  address: ''
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await merchantStore.register(form.name, form.phone, form.password, form.address || undefined)
      ElMessage.success('入驻申请已提交，请等待审核')
      router.push('/merchant/login')
    } catch (e) {
      // 错误提示
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 40px auto 0;
  padding: 20px;
}
.logo { text-align: center; margin-bottom: 20px; }
.register-btn { width: 100%; }
.login-link { text-align: center; margin-top: 20px; }
</style>