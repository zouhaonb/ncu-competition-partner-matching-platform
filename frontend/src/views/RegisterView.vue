<!-- 注册页面 - 左右分栏布局 -->
<template>
  <div class="register-page">
    <!-- 左侧品牌区 -->
    <div class="register-left">
      <div class="brand-content">
        <div class="brand-icon">
          <img src="/校徽.png" alt="校徽" class="brand-logo-img" />
        </div>
        <h1 class="brand-title">南昌大学</h1>
        <h2 class="brand-subtitle">竞赛队友匹配平台</h2>
        <p class="brand-desc">加入我们，开启你的竞赛之旅</p>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="register-right">
      <div class="form-wrapper">
        <div class="form-header">
          <h3>创建账号</h3>
          <p>填写信息加入南昌大学竞赛队友匹配平台</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" size="large" label-position="top">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="学号" prop="studentId">
                <el-input v-model="form.studentId" placeholder="请输入学号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入姓名" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" placeholder="至少6位密码" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="再次输入密码" show-password />
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="form.phone" placeholder="选填" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="QQ号" prop="qq">
                <el-input v-model="form.qq" placeholder="选填" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item>
            <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister">注 册</el-button>
          </el-form-item>
        </el-form>
        <div class="form-footer">
          <span>已有账号？</span>
          <router-link to="/login" class="link">去登录</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  studentId: '',
  name: '',
  password: '',
  confirmPassword: '',
  phone: '',
  qq: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次密码输入不一致'))
  } else {
    callback()
  }
}

const rules = {
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await register({
      studentId: form.studentId,
      name: form.name,
      password: form.password,
      phone: form.phone,
      qq: form.qq
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  display: flex;
  min-height: calc(100vh - 56px);
  margin: -20px;
}

/* ===== 左侧品牌区 ===== */
.register-left {
  flex: 0 0 40%;
  background: linear-gradient(160deg, #0d47a1 0%, #1565c0 30%, #1a73e8 60%, #2196f3 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  position: relative;
  overflow: hidden;
}
.register-left::before {
  content: '';
  position: absolute;
  top: -40%;
  right: -20%;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  background: rgba(255,255,255,0.03);
}
.register-left::after {
  content: '';
  position: absolute;
  bottom: -20%;
  left: -10%;
  width: 250px;
  height: 250px;
  border-radius: 50%;
  background: rgba(255,255,255,0.04);
}
.brand-content {
  text-align: center;
  color: #fff;
  position: relative;
  z-index: 1;
}
.brand-icon {
  margin-bottom: 20px;
  width: 90px;
  height: 90px;
  border-radius: 50%;
  background: rgba(255,255,255,0.12);
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.brand-logo-img {
  width: 64px;
  height: 64px;
  object-fit: contain;
}
.brand-title {
  font-size: 34px;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: 4px;
}
.brand-subtitle {
  font-size: 20px;
  font-weight: 400;
  margin: 0 0 24px 0;
  opacity: 0.9;
  letter-spacing: 2px;
}
.brand-desc {
  font-size: 15px;
  opacity: 0.75;
  line-height: 1.8;
  margin: 0;
  max-width: 300px;
}

/* ===== 右侧表单区 ===== */
.register-right {
  flex: 0 0 60%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  padding: 40px;
}
.form-wrapper {
  width: 100%;
  max-width: 500px;
}
.form-header {
  text-align: center;
  margin-bottom: 28px;
}
.form-header h3 {
  font-size: 26px;
  color: #1a1a1a;
  margin: 0 0 8px 0;
  font-weight: 600;
}
.form-header p {
  color: #909399;
  font-size: 14px;
  margin: 0;
}
.submit-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
  border-radius: 8px;
  margin-top: 4px;
}
.form-footer {
  text-align: center;
  color: #909399;
  font-size: 14px;
}
.form-footer .link {
  color: #1a73e8;
  text-decoration: none;
  font-weight: 500;
}
.form-footer .link:hover {
  text-decoration: underline;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .register-page {
    flex-direction: column;
  }
  .register-left {
    flex: 0 0 auto;
    padding: 32px 24px;
  }
  .brand-icon {
    width: 60px;
    height: 60px;
    margin-bottom: 12px;
  }
  .brand-logo-img {
    width: 42px;
    height: 42px;
  }
  .brand-title {
    font-size: 24px;
  }
  .brand-subtitle {
    font-size: 15px;
  }
  .brand-desc {
    font-size: 13px;
  }
  .register-right {
    flex: 1;
    padding: 24px 20px;
  }
}
</style>
