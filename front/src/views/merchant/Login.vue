<template>
  <div class="login-container">
    <div class="logo">
      <h1>商家登录</h1>
      <p>外卖配送平台</p>
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item prop="phone">
        <el-input v-model="form.phone" placeholder="手机号" />
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="form.password" type="password" placeholder="密码" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" size="large" round :loading="loading" class="login-btn" @click="handleLogin">
          登录
        </el-button>
      </el-form-item>
    </el-form>
    <div class="register-link">
      还没有店铺？<router-link to="/merchant/register">立即入驻</router-link>
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

const form = reactive({ phone: '', password: '' })
const rules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await merchantStore.login(form.phone, form.password)
      ElMessage.success('登录成功')
      router.push('/merchant/dashboard')
    } catch (e: any) {
      // 错误已在拦截器处理
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 60px auto 0;
  padding: 20px;
}
.logo {
  text-align: center;
  margin-bottom: 30px;
}
.login-btn {
  width: 100%;
}
.register-link {
  text-align: center;
  margin-top: 20px;
}
</style>