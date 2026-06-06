<!-- 技术栈：Vue 3, Element Plus, Pinia, Vue Router, TypeScript -->
<template>
  <div class="register-container">
    <div class="logo">
      <h1>创建账号</h1>
      <p>开启美食之旅</p>
    </div>
    <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-position="top"
        class="register-form"
    >
      <el-form-item prop="phone">
        <el-input
            v-model="form.phone"
            placeholder="请输入手机号"
            maxlength="11"
            clearable
        />
      </el-form-item>
      <el-form-item prop="nickname">
        <el-input
            v-model="form.nickname"
            placeholder="请输入昵称（选填）"
            maxlength="20"
            clearable
        />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
        />
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请确认密码"
            show-password
        />
      </el-form-item>
      <el-form-item>
        <el-button
            type="primary"
            size="large"
            :loading="loading"
            round
            class="register-btn"
            @click="handleRegister"
        >
          注册
        </el-button>
      </el-form-item>
    </el-form>
    <div class="login-link">
      已有账号？ <router-link to="/login">立即登录</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({
  phone: '',
  nickname: '',
  password: '',
  confirmPassword: ''
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

const validateConfirmPassword = (_rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  phone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }]
}

const handleRegister = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await userStore.register(form.phone, form.password, form.nickname || undefined)
      ElMessage.success('注册成功')
      router.push('/login')
    } catch (error: any) {
      // 错误提示已在拦截器处理
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-container {
  padding: 40px 20px 20px;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.logo {
  text-align: center;
  margin-bottom: 30px;
}
.logo h1 {
  font-size: 28px;
  color: #303133;
  margin: 0 0 8px;
}
.logo p {
  color: #909399;
  margin: 0;
}
.register-form {
  background: #fff;
  padding: 30px 20px;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
}
.register-btn {
  width: 100%;
  margin-top: 10px;
}
.login-link {
  text-align: center;
  margin-top: 20px;
  color: #909399;
}
.login-link a {
  color: #409eff;
  text-decoration: none;
}
</style>