<!-- 管理员页面 - 竞赛类别管理、用户管理、招募管理 -->
<template>
  <div class="admin-container">
    <h2 class="page-title">后台管理</h2>
    <el-tabs v-model="activeTab" type="border-card" @tab-change="handleTabChange">
      <!-- 竞赛类别管理 -->
      <el-tab-pane label="竞赛类别管理" name="categories">
        <div class="admin-toolbar">
          <el-input v-model="newCategoryName" placeholder="输入新类别名称" style="width:240px" @keyup.enter="handleAddCategory" />
          <el-button type="primary" style="margin-left:12px" :loading="catAdding" @click="handleAddCategory">添加类别</el-button>
        </div>
        <el-table :data="categories" v-loading="catLoading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="类别名称" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="danger" size="small" @click="handleDeleteCategory(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 用户管理 -->
      <el-tab-pane label="用户管理" name="users">
        <el-table :data="users" v-loading="userLoading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="studentId" label="学号" width="140" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="role" label="角色" width="80">
            <template #default="{ row }">
              <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" size="small">{{ row.role === 'ADMIN' ? '管理员' : '用户' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="手机" width="140" />
          <el-table-column prop="qq" label="QQ" width="120" />
          <el-table-column prop="intro" label="简介" min-width="160" />
          <el-table-column prop="createTime" label="注册时间" width="170" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" size="small" :disabled="row.role === 'ADMIN'" @click="handleDeleteUser(row.id, row.name)">
                {{ row.role === 'ADMIN' ? '不可删' : '删除' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- 招募管理 -->
      <el-tab-pane label="招募管理" name="recruitments">
        <el-table :data="recruitments" v-loading="recLoading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'OPEN' ? 'success' : 'info'" size="small">{{ row.status === 'OPEN' ? '招募中' : '已关闭' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" width="170" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button v-if="row.status === 'OPEN'" type="warning" size="small" @click="handleCloseRecruitment(row.id)">关闭</el-button>
              <el-button type="danger" size="small" @click="handleDeleteRecruitment(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminCategories, addCategory, deleteCategory, getAdminUsers, getAdminRecruitments, deleteUser, closeRecruitmentAdmin, deleteRecruitment } from '../api/admin'

const activeTab = ref('categories')
// 类别
const categories = ref([])
const catLoading = ref(false)
const catAdding = ref(false)
const newCategoryName = ref('')
// 用户
const users = ref([])
const userLoading = ref(false)
// 招募
const recruitments = ref([])
const recLoading = ref(false)

const fetchCategories = async () => {
  catLoading.value = true
  try { const res = await getAdminCategories(); categories.value = res.data } catch (e) { /* ignore */ }
  finally { catLoading.value = false }
}

const handleAddCategory = async () => {
  if (!newCategoryName.value.trim()) { ElMessage.warning('请输入类别名称'); return }
  catAdding.value = true
  try { await addCategory({ name: newCategoryName.value.trim() }); ElMessage.success('添加成功'); newCategoryName.value = ''; fetchCategories() }
  catch (e) { /* ignore */ }
  finally { catAdding.value = false }
}

const handleDeleteCategory = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该类别？', '提示', { type: 'warning' })
    await deleteCategory(id)
    ElMessage.success('删除成功')
    fetchCategories()
  } catch (e) { /* ignore */ }
}

const fetchUsers = async () => {
  userLoading.value = true
  try { const res = await getAdminUsers(); users.value = res.data } catch (e) { /* ignore */ }
  finally { userLoading.value = false }
}

const handleDeleteUser = async (id, name) => {
  try {
    await ElMessageBox.confirm(`确定删除用户「${name}」吗？其招募和申请数据也将被清理。`, '提示', { type: 'warning', confirmButtonText: '确定删除' })
    await deleteUser(id)
    ElMessage.success('用户已删除')
    fetchUsers()
  } catch (e) { /* ignore */ }
}

const fetchRecruitments = async () => {
  recLoading.value = true
  try { const res = await getAdminRecruitments(); recruitments.value = res.data } catch (e) { /* ignore */ }
  finally { recLoading.value = false }
}

const handleCloseRecruitment = async (id) => {
  try {
    await ElMessageBox.confirm('确定关闭该招募吗？', '提示', { type: 'warning' })
    await closeRecruitmentAdmin(id)
    ElMessage.success('招募已关闭')
    fetchRecruitments()
  } catch (e) { /* ignore */ }
}

const handleDeleteRecruitment = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该招募吗？所有相关申请也将被删除，此操作不可恢复。', '警告', { type: 'warning', confirmButtonText: '确定删除' })
    await deleteRecruitment(id)
    ElMessage.success('招募已删除')
    fetchRecruitments()
  } catch (e) { /* ignore */ }
}

// 切换标签加载数据
const handleTabChange = (name) => {
  if (name === 'categories') fetchCategories()
  else if (name === 'users') fetchUsers()
  else if (name === 'recruitments') fetchRecruitments()
}

onMounted(fetchCategories)
</script>

<style scoped>
.admin-container { max-width: 1100px; margin: 0 auto; padding-bottom: 40px; }
.page-title { font-size: 22px; margin-bottom: 20px; color: #303133; }
.admin-toolbar { display: flex; align-items: center; margin-bottom: 16px; }
</style>
