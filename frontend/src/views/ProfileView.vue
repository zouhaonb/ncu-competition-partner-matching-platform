<!-- 个人中心页面 - 标签页切换：个人信息、技能标签、我的招募、我的申请、我的队友 -->
<template>
  <div class="profile-container">
    <h2 class="page-title">个人中心</h2>
    <el-tabs v-model="activeTab" type="border-card" @tab-change="loadTabData">
      <!-- 个人信息 -->
      <el-tab-pane label="个人信息" name="info">
        <el-form :model="profileForm" label-width="100px" style="max-width:480px">
          <el-form-item label="学号">
            <el-input :model-value="profile.studentId" disabled />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="profileForm.name" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="profileForm.phone" />
          </el-form-item>
          <el-form-item label="QQ">
            <el-input v-model="profileForm.qq" />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input v-model="profileForm.intro" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- 技能标签 -->
      <el-tab-pane label="技能标签" name="tags">
        <div class="tag-section">
          <div class="add-tag-row">
            <el-select v-model="selectedTagId" placeholder="选择技能标签" style="width:200px">
              <el-option v-for="tag in availableTags" :key="tag.id" :label="tag.name" :value="tag.id" />
            </el-select>
            <el-select v-model="selectedProficiency" placeholder="熟练度" style="width:120px; margin-left:12px">
              <el-option label="了解" :value="1" />
              <el-option label="掌握" :value="2" />
              <el-option label="精通" :value="3" />
            </el-select>
            <el-button type="primary" style="margin-left:12px" @click="addTag">添加</el-button>
          </div>
          <div class="current-tags" v-if="userTags.length">
            <el-tag v-for="ut in userTags" :key="ut.id" closable size="large" style="margin:6px"
              :type="ut.proficiency === 3 ? '' : ut.proficiency === 2 ? 'success' : 'info'"
              @close="removeTag(ut.id)">
              {{ ut.tagName }}（{{ ut.proficiency === 3 ? '精通' : ut.proficiency === 2 ? '掌握' : '了解' }}）
            </el-tag>
          </div>
          <el-empty v-else description="暂无技能标签，请添加" />
        </div>
      </el-tab-pane>

      <!-- 我的招募 -->
      <el-tab-pane label="我的招募" name="recruitments">
        <el-table :data="myRecruitments" v-loading="recruitmentLoading">
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="categoryName" label="类别" width="120" />
          <el-table-column prop="requiredNumber" label="需人数" width="80" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'OPEN' ? 'success' : 'info'" size="small">{{ row.status === 'OPEN' ? '招募中' : '已关闭' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" width="170" />
        </el-table>
      </el-tab-pane>

      <!-- 我的申请 -->
      <el-tab-pane label="我的申请" name="applications">
        <el-table :data="myApplications" v-loading="appLoading">
          <el-table-column prop="recruitmentTitle" label="招募标题" />
          <el-table-column prop="reason" label="申请理由" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ACCEPTED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'" size="small">
                {{ row.status === 'ACCEPTED' ? '已通过' : row.status === 'REJECTED' ? '已拒绝' : '待审核' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="applyTime" label="申请时间" width="170" />
        </el-table>
      </el-tab-pane>

      <!-- 我的队友 -->
      <el-tab-pane label="我的队友" name="teammates">
        <div class="teammate-grid" v-if="teammates.length">
          <div v-for="tm in teammates" :key="tm.id" class="teammate-card">
            <el-avatar :size="48" style="background:#1a73e8">{{ tm.name?.charAt(0) }}</el-avatar>
            <div class="tm-info">
              <h4>{{ tm.name }}</h4>
              <p>学号：{{ tm.studentId }}</p>
              <p>手机：{{ tm.phone || '未填写' }}</p>
              <p>QQ：{{ tm.qq || '未填写' }}</p>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无队友" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserProfile, updateUserProfile, getUserRecruitments, getUserApplications, getUserTeammates } from '../api/user'
import { getAllTags, getUserTags, addUserTag, deleteUserTag } from '../api/tag'

const activeTab = ref('info')
const profile = ref({})
const profileForm = reactive({ name: '', phone: '', qq: '', intro: '' })
const saving = ref(false)

const allTags = ref([])
const userTags = ref([])
const selectedTagId = ref(null)
const selectedProficiency = ref(1)

const myRecruitments = ref([])
const myApplications = ref([])
const teammates = ref([])
const recruitmentLoading = ref(false)
const appLoading = ref(false)

const availableTags = ref([])

const fetchProfile = async () => {
  try {
    const res = await getUserProfile()
    profile.value = res.data
    Object.assign(profileForm, { name: res.data.name || '', phone: res.data.phone || '', qq: res.data.qq || '', intro: res.data.intro || '' })
  } catch (e) { /* ignore */ }
}

const saveProfile = async () => {
  saving.value = true
  try {
    await updateUserProfile(profileForm)
    ElMessage.success('保存成功')
  } catch (e) { /* ignore */ }
  finally { saving.value = false }
}

const fetchAllTags = async () => {
  try {
    const res = await getAllTags()
    allTags.value = res.data
    availableTags.value = res.data
  } catch (e) { /* ignore */ }
}

const fetchUserTags = async () => {
  try {
    const res = await getUserTags()
    userTags.value = res.data
  } catch (e) { /* ignore */ }
}

const addTag = async () => {
  if (!selectedTagId.value) { ElMessage.warning('请选择标签'); return }
  try {
    await addUserTag({ tagId: selectedTagId.value, proficiency: selectedProficiency.value })
    ElMessage.success('标签添加成功')
    selectedTagId.value = null
    selectedProficiency.value = 1
    fetchUserTags()
  } catch (e) { /* ignore */ }
}

const removeTag = async (id) => {
  try {
    await deleteUserTag(id)
    ElMessage.success('标签已删除')
    fetchUserTags()
  } catch (e) { /* ignore */ }
}

const fetchMyRecruitments = async () => {
  recruitmentLoading.value = true
  try {
    const res = await getUserRecruitments()
    myRecruitments.value = res.data
  } catch (e) { /* ignore */ }
  finally { recruitmentLoading.value = false }
}

const fetchMyApplications = async () => {
  appLoading.value = true
  try {
    const res = await getUserApplications()
    myApplications.value = res.data
  } catch (e) { /* ignore */ }
  finally { appLoading.value = false }
}

const fetchTeammates = async () => {
  try {
    const res = await getUserTeammates()
    teammates.value = res.data
  } catch (e) { /* ignore */ }
}

// 切换标签时加载对应数据
const loadTabData = (tabName) => {
  if (tabName === 'recruitments') fetchMyRecruitments()
  else if (tabName === 'applications') fetchMyApplications()
  else if (tabName === 'teammates') fetchTeammates()
  else if (tabName === 'tags') { fetchAllTags(); fetchUserTags() }
}

onMounted(() => {
  fetchProfile()
  fetchAllTags()
  fetchUserTags()
})
</script>

<style scoped>
.profile-container { max-width: 900px; margin: 0 auto; padding-bottom: 40px; }
.page-title { font-size: 22px; margin-bottom: 20px; color: #303133; }
.tag-section { padding: 12px 0; }
.add-tag-row { display: flex; align-items: center; margin-bottom: 16px; }
.current-tags { min-height: 60px; }
.teammate-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; }
.teammate-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fafbfc;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}
.tm-info h4 { margin: 0 0 4px 0; }
.tm-info p { margin: 2px 0; font-size: 13px; color: #606266; }
</style>
