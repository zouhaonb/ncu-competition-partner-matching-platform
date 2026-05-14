<!-- 招募详情页 - 展示招募信息、推荐队友、申请功能 -->
<template>
  <div class="detail-container" v-loading="loading">
    <!-- 招募详情卡片 -->
    <el-card class="detail-card" v-if="recruitment">
      <template #header>
        <div class="detail-header">
          <h2>{{ recruitment.title }}</h2>
          <el-tag :type="recruitment.status === 'OPEN' ? 'success' : 'info'" size="large">{{ recruitment.status === 'OPEN' ? '招募中' : '已关闭' }}</el-tag>
        </div>
      </template>
      <div class="detail-body">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="发布者">{{ recruitment.publisherName }}</el-descriptions-item>
          <el-descriptions-item label="竞赛类别">{{ recruitment.categoryName }}</el-descriptions-item>
          <el-descriptions-item label="需要人数">{{ recruitment.requiredNumber }} 人</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ recruitment.createTime?.substring(0, 10) }}</el-descriptions-item>
          <el-descriptions-item label="招募描述" :span="2">{{ recruitment.description || '暂无描述' }}</el-descriptions-item>
        </el-descriptions>
        <div class="required-tags" v-if="recruitment.requiredTags?.length">
          <span class="tags-label">所需技能：</span>
          <el-tag v-for="tag in recruitment.requiredTags" :key="tag.id" style="margin-right: 8px">{{ tag.name }}</el-tag>
        </div>
        <!-- 操作按钮 -->
        <div class="action-buttons" v-if="currentUserId">
          <el-button v-if="!isPublisher && recruitment.status === 'OPEN'" type="primary" size="large" :icon="Plus" @click="showApplyDialog = true">申请加入</el-button>
          <el-button v-if="isPublisher && recruitment.status === 'OPEN'" type="warning" size="large" @click="handleClose">关闭招募</el-button>
          <el-button v-if="isPublisher" type="info" size="large" @click="toggleApplications">
            {{ showApplications ? '隐藏' : '查看' }}申请列表
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 申请列表（发布者可见） -->
    <el-card class="applications-card" v-if="showApplications">
      <template #header><h3>申请列表</h3></template>
      <el-table :data="applications" v-loading="appLoading">
        <el-table-column prop="applicantName" label="申请人" />
        <el-table-column prop="reason" label="申请理由" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACCEPTED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'" size="small">
              {{ row.status === 'ACCEPTED' ? '已通过' : row.status === 'REJECTED' ? '已拒绝' : '待审核' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyTime" label="申请时间" width="170" />
        <el-table-column label="操作" width="180" v-if="isPublisher">
          <template #default="{ row }">
            <el-button v-if="row.status === 'PENDING'" type="success" size="small" @click="handleApplication(row.id, 'ACCEPTED')">同意</el-button>
            <el-button v-if="row.status === 'PENDING'" type="danger" size="small" @click="handleApplication(row.id, 'REJECTED')">拒绝</el-button>
            <span v-if="row.status === 'ACCEPTED'" style="color:#67c23a">已同意，联系方式可见</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 推荐队友 -->
    <el-card class="recommend-card">
      <template #header><h3>推荐队友 <span style="font-weight:400;font-size:13px;color:#909399">（基于技能匹配度）</span></h3></template>
      <div class="recommend-grid" v-if="recommendations.length">
        <div v-for="rec in recommendations" :key="rec.userId" class="recommend-item">
          <div class="recommend-rank">
            <el-progress type="circle" :percentage="Math.round(rec.matchScore)" :width="80"
              :color="rec.matchScore >= 70 ? '#67c23a' : rec.matchScore >= 40 ? '#e6a23c' : '#909399'" />
          </div>
          <div class="recommend-info">
            <h4>{{ rec.userName }}</h4>
            <p class="student-id">学号：{{ rec.userStudentId }}</p>
            <div class="match-tags">
              <el-tag v-for="tag in rec.matchTags" :key="tag.tagName" size="small" :type="tag.proficiency === 3 ? '' : tag.proficiency === 2 ? 'success' : 'info'">
                {{ tag.tagName }}
                <span style="font-size:11px">({{ tag.proficiency === 3 ? '精通' : tag.proficiency === 2 ? '掌握' : '了解' }})</span>
              </el-tag>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无推荐队友" />
    </el-card>

    <!-- 申请弹窗 -->
    <el-dialog v-model="showApplyDialog" title="申请加入" width="440px">
      <el-form ref="applyFormRef" :model="applyForm" :rules="applyRules">
        <el-form-item label="申请理由" prop="reason">
          <el-input v-model="applyForm.reason" type="textarea" :rows="4" placeholder="介绍你的技能和参赛经历，让队长更快了解你" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showApplyDialog = false">取消</el-button>
        <el-button type="primary" :loading="applyLoading" @click="handleApply">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRecruitmentDetail, getRecommendations, applyRecruitment, getApplications, closeRecruitment, handleApplication as handleApplicationApi } from '../api/recruitment'

const route = useRoute()
const recruitment = ref(null)
const recommendations = ref([])
const applications = ref([])
const loading = ref(false)
const appLoading = ref(false)
const showApplications = ref(false)
const showApplyDialog = ref(false)
const applyLoading = ref(false)
const applyFormRef = ref(null)

const applyForm = reactive({ reason: '' })
const applyRules = { reason: [{ required: true, message: '请输入申请理由', trigger: 'blur' }] }

const currentUserId = computed(() => Number(localStorage.getItem('userId') || 0))
const isPublisher = computed(() => recruitment.value?.publisherId === currentUserId.value)

const fetchDetail = async () => {
  loading.value = true
  try {
    const res = await getRecruitmentDetail(route.params.id)
    recruitment.value = res.data
  } catch (e) { /* ignore */ }
  finally { loading.value = false }
}

const fetchRecommendations = async () => {
  try {
    const res = await getRecommendations(route.params.id)
    recommendations.value = res.data
  } catch (e) { /* ignore */ }
}

const fetchApplications = async () => {
  appLoading.value = true
  try {
    const res = await getApplications(route.params.id)
    applications.value = res.data
  } catch (e) { /* ignore */ }
  finally { appLoading.value = false }
}

const toggleApplications = () => {
  showApplications.value = !showApplications.value
  if (showApplications.value) {
    fetchApplications()
  }
}

const handleApply = async () => {
  const valid = await applyFormRef.value.validate().catch(() => false)
  if (!valid) return
  applyLoading.value = true
  try {
    await applyRecruitment(route.params.id, applyForm)
    ElMessage.success('申请已提交')
    showApplyDialog.value = false
    applyForm.reason = ''
    fetchRecommendations()
  } catch (e) { /* ignore */ }
  finally { applyLoading.value = false }
}

const handleApplication = async (applicationId, status) => {
  try {
    await handleApplicationApi(applicationId, { status })
    ElMessage.success(status === 'ACCEPTED' ? '已同意申请' : '已拒绝申请')
    fetchApplications()
    fetchRecommendations()
  } catch (e) { /* ignore */ }
}

const handleClose = async () => {
  try {
    await ElMessageBox.confirm('确定要关闭该招募吗？关闭后无法重新开启。', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await closeRecruitment(route.params.id)
    ElMessage.success('招募已关闭')
    fetchDetail()
  } catch (e) { /* ignore */ }
}

onMounted(() => {
  fetchDetail()
  fetchRecommendations()
})
</script>

<style scoped>
.detail-container { max-width: 900px; margin: 0 auto; padding-bottom: 40px; }
.detail-card { margin-bottom: 20px; }
.detail-header { display: flex; justify-content: space-between; align-items: center; }
.detail-header h2 { margin: 0; font-size: 20px; }
.detail-body { margin-top: 8px; }
.required-tags { margin-top: 16px; }
.tags-label { font-size: 14px; color: #606266; margin-right: 8px; }
.action-buttons { margin-top: 20px; display: flex; gap: 12px; }
.applications-card { margin-bottom: 20px; }
.recommend-card { margin-bottom: 20px; }
.recommend-grid { display: flex; flex-direction: column; gap: 12px; }
.recommend-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 16px;
  background: #fafbfc;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  transition: all 0.2s;
}
.recommend-item:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
.recommend-info h4 { margin: 0 0 4px 0; font-size: 16px; }
.student-id { margin: 0 0 8px 0; color: #909399; font-size: 13px; }
.match-tags { display: flex; flex-wrap: wrap; gap: 6px; }
</style>
