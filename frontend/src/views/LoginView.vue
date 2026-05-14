<!-- 登录页面 - 左右分栏布局 -->
<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="login-left">
      <div class="brand-content">
        <div class="brand-icon">
          <img src="/校徽.png" alt="校徽" class="brand-logo-img" />
        </div>
        <h1 class="brand-title">南昌大学</h1>
        <h2 class="brand-subtitle">竞赛队友匹配平台</h2>
        <p class="brand-desc">汇聚校园英才，精准匹配队友，让每一次竞赛都不再孤单</p>
        <div class="brand-features">
          <div class="feature-item">
            <el-icon><Search /></el-icon>
            <span>智能匹配推荐</span>
          </div>
          <div class="feature-item">
            <el-icon><UserFilled /></el-icon>
            <span>技能标签体系</span>
          </div>
          <div class="feature-item">
            <el-icon><Connection /></el-icon>
            <span>高效组队协作</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="login-right">
      <div class="form-wrapper">
        <div class="form-header">
          <h3>欢迎回来</h3>
          <p>请使用学号登录你的账号</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" size="large">
          <el-form-item prop="studentId">
            <el-input v-model="form.studentId" placeholder="请输入学号" :prefix-icon="User" clearable />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password :prefix-icon="Lock" @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">登 录</el-button>
          </el-form-item>
        </el-form>
        <div class="form-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="link">立即注册</router-link>
        </div>
        <div class="test-hint">
          <el-divider><span style="color:#c0c4cc;font-size:12px">测试账号</span></el-divider>
          <p>8008123001 / 123456</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, Search, UserFilled, Connection } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  studentId: '',
  password: ''
})

const rules = {
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login(form)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userId', res.data.userId)
    localStorage.setItem('userName', res.data.name)
    localStorage.setItem('studentId', res.data.studentId)
    localStorage.setItem('userRole', res.data.role || 'USER')
    ElMessage.success('登录成功')
    router.push('/square')
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  min-height: calc(100vh - 56px);
  margin: -20px;
}

/* ===== 左侧品牌区 ===== */
.login-left {
  flex: 0 0 44%;
  background: linear-gradient(160deg, #0d47a1 0%, #1565c0 30%, #1a73e8 60%, #2196f3 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 40px;
  position: relative;
  overflow: hidden;
}
.login-left::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: rgba(255,255,255,0.03);
}
.login-left::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -10%;
  width: 300px;
  height: 300px;
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
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: rgba(255,255,255,0.12);
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.brand-logo-img {
  width: 72px;
  height: 72px;
  object-fit: contain;
}
.brand-title {
  font-size: 38px;
  font-weight: 700;
  margin: 0 0 8px 0;
  letter-spacing: 4px;
}
.brand-subtitle {
  font-size: 22px;
  font-weight: 400;
  margin: 0 0 24px 0;
  opacity: 0.9;
  letter-spacing: 2px;
}
.brand-desc {
  font-size: 15px;
  opacity: 0.75;
  line-height: 1.8;
  margin: 0 0 40px 0;
  max-width: 360px;
}
.brand-features {
  display: flex;
  flex-direction: column;
  gap: 14px;
  align-items: center;
}
.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  opacity: 0.8;
  padding: 8px 20px;
  background: rgba(255,255,255,0.08);
  border-radius: 20px;
}

/* ===== 右侧表单区 ===== */
.login-right {
  flex: 0 0 56%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  padding: 40px;
}
.form-wrapper {
  width: 100%;
  max-width: 400px;
}
.form-header {
  text-align: center;
  margin-bottom: 32px;
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
}
.form-footer {
  text-align: center;
  color: #909399;
  font-size: 14px;
  margin-top: 8px;
}
.form-footer .link {
  color: #1a73e8;
  text-decoration: none;
  font-weight: 500;
}
.form-footer .link:hover {
  text-decoration: underline;
}
.test-hint {
  margin-top: 28px;
  text-align: center;
}
.test-hint p {
  margin: 0;
  color: #c0c4cc;
  font-size: 13px;
}

/* ===== 响应式 ===== */
@media (max-width: 768px) {
  .login-page {
    flex-direction: column;
  }
  .login-left {
    flex: 0 0 auto;
    padding: 40px 24px;
  }
  .brand-title {
    font-size: 28px;
  }
  .brand-subtitle {
    font-size: 17px;
  }
  .brand-desc {
    font-size: 13px;
    margin-bottom: 24px;
  }
  .brand-features {
    display: none;
  }
  .login-right {
    flex: 1;
    padding: 32px 24px;
  }
}
</style>
