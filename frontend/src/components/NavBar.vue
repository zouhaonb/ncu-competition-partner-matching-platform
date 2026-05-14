<!-- NavBar.vue - 顶部导航栏 -->
<template>
  <header class="navbar">
    <div class="navbar-inner">
      <!-- 左侧 Logo 和标题 -->
      <div class="navbar-brand" @click="$router.push('/square')">
        <img src="/校徽.png" class="brand-logo" alt="校徽" />
        <span class="brand-title">南昌大学竞赛队友匹配平台</span>
      </div>

      <!-- 中间导航链接 -->
      <el-menu
        mode="horizontal"
        :default-active="activeMenu"
        :router="true"
        class="navbar-menu"
        background-color="transparent"
        text-color="rgba(255,255,255,0.85)"
        active-text-color="#ffffff"
      >
        <el-menu-item index="/">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/square">
          <el-icon><Search /></el-icon>
          <span>招募广场</span>
        </el-menu-item>
        <el-menu-item index="/profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
        <el-menu-item v-if="isAdmin" index="/admin">
          <el-icon><Setting /></el-icon>
          <span>管理后台</span>
        </el-menu-item>
      </el-menu>

      <!-- 右侧用户操作区 -->
      <div class="navbar-actions">
        <template v-if="isLoggedIn">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="user-name">{{ userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="$router.push('/profile')">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" plain size="small" @click="$router.push('/login')">
            登录
          </el-button>
          <el-button type="success" plain size="small" @click="$router.push('/register')">
            注册
          </el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { HomeFilled, Search, User, Setting, ArrowDown, SwitchButton, UserFilled } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

// 当前激活的菜单项
const activeMenu = computed(() => {
  if (route.path.startsWith('/recruitment')) return '/square'
  return route.path
})

// 读取 localStorage 的辅助函数
const readAuth = () => {
  isLoggedIn.value = !!localStorage.getItem('token')
  userName.value = localStorage.getItem('userName') || '用户'
  isAdmin.value = localStorage.getItem('userRole') === 'ADMIN'
}

// 用户状态 — 用 ref（不是 computed）因为 localStorage 不是响应式的
const isLoggedIn = ref(!!localStorage.getItem('token'))
const userName = ref(localStorage.getItem('userName') || '用户')
const isAdmin = ref(localStorage.getItem('userRole') === 'ADMIN')

// 路由变化时重新读取 localStorage（登录/退出后都会跳转路由）
watch(() => route.path, readAuth)

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('userName')
  localStorage.removeItem('studentId')
  localStorage.removeItem('userRole')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.navbar {
  background: linear-gradient(135deg, #1a73e8 0%, #1557b0 100%);
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(26, 115, 232, 0.3);
}

.navbar-inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 56px;
  padding: 0 20px;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
  margin-right: 24px;
  user-select: none;
}

.brand-logo {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.brand-title {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
  white-space: nowrap;
}

.navbar-menu {
  flex: 1;
  border-bottom: none !important;
}

.navbar-menu .el-menu-item {
  border-bottom: 2px solid transparent;
  height: 56px;
  line-height: 56px;
}

.navbar-menu .el-menu-item.is-active {
  border-bottom-color: #fff;
  background-color: rgba(255, 255, 255, 0.15) !important;
}

.navbar-menu .el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1) !important;
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  margin-left: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #fff;
  font-size: 14px;
}

.user-name {
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.navbar-actions .el-button--primary.is-plain {
  --el-button-bg-color: rgba(255,255,255,0.2);
  --el-button-border-color: rgba(255,255,255,0.5);
  --el-button-text-color: #fff;
}
.navbar-actions .el-button--success.is-plain {
  --el-button-bg-color: rgba(255,255,255,0.2);
  --el-button-border-color: rgba(255,255,255,0.5);
  --el-button-text-color: #fff;
}
</style>
