<!-- 首页 - 登录后落地页，展示统计概览和推荐招募 -->
<template>
  <div class="home-container">
    <!-- 欢迎区 -->
    <div class="welcome-section">
      <h1 class="welcome-title">欢迎回来，{{ userName }}</h1>
      <p class="welcome-sub">以下是你的竞赛队友匹配概览</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background:#e8f4fd">
          <el-icon :size="28" color="#1a73e8"><Postcard /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ stats.myRecruitments }}</div>
          <div class="stat-label">我的招募</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#fef0e6">
          <el-icon :size="28" color="#e6a23c"><Document /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ stats.myApplications }}</div>
          <div class="stat-label">我的申请</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#e8f8e8">
          <el-icon :size="28" color="#67c23a"><UserFilled /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ stats.myTeammates }}</div>
          <div class="stat-label">我的队友</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background:#f4e8fd">
          <el-icon :size="28" color="#8b5cf6"><DataAnalysis /></el-icon>
        </div>
        <div class="stat-body">
          <div class="stat-value">{{ stats.platformRecruitments }}</div>
          <div class="stat-label">平台招募总数</div>
        </div>
      </div>
    </div>

    <!-- 推荐招募 -->
    <div class="section">
      <div class="section-header">
        <h3>推荐给我的招募</h3>
        <span class="section-tip">基于你的技能标签智能匹配</span>
      </div>
      <div class="recommend-grid" v-loading="recLoading">
        <div v-for="rec in recommendedRecruitments" :key="rec.recruitmentId" class="recommend-card" @click="$router.push(`/recruitment/${rec.recruitmentId}`)">
          <div class="rec-card-header">
            <div class="rec-match">
              <el-progress type="circle" :percentage="Math.round(rec.matchScore)" :width="56"
                :color="rec.matchScore >= 70 ? '#67c23a' : rec.matchScore >= 40 ? '#e6a23c' : '#909399'"
                :stroke-width="6" />
            </div>
            <div class="rec-title-wrap">
              <h4>{{ rec.title }}</h4>
              <span class="rec-category"><el-icon><Collection /></el-icon> {{ rec.categoryName }}</span>
            </div>
          </div>
          <div class="rec-tags">
            <el-tag v-for="tag in rec.requiredTagNames" :key="tag" size="small" style="margin-right:6px">{{ tag }}</el-tag>
          </div>
          <div class="rec-footer">
            <span><el-icon><User /></el-icon> {{ rec.publisherName }}</span>
            <span>{{ rec.createTime?.substring(0, 10) }}</span>
          </div>
        </div>
        <el-empty v-if="!recLoading && recommendedRecruitments.length === 0" description="暂无推荐，先添加技能标签或等待更多招募发布" />
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="section">
      <div class="section-header"><h3>快捷入口</h3></div>
      <div class="quick-actions">
        <el-button type="primary" :icon="Plus" size="large" @click="$router.push('/square'); setTimeout(() => document.querySelector('.el-button--primary')?.click?.(), 100)">
          发布招募
        </el-button>
        <el-button :icon="Edit" size="large" @click="$router.push('/profile')">
          完善技能标签
        </el-button>
        <el-button :icon="Search" size="large" @click="$router.push('/square')">
          浏览招募广场
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Postcard, Document, UserFilled, DataAnalysis, Collection, User, Plus, Edit, Search } from '@element-plus/icons-vue'
import { getUserRecruitments, getUserApplications, getUserTeammates } from '../api/user'
import { getRecruitments, getRecommendedRecruitments } from '../api/recruitment'

const router = useRouter()
const userName = ref(localStorage.getItem('userName') || '用户')

const stats = reactive({
  myRecruitments: 0,
  myApplications: 0,
  myTeammates: 0,
  platformRecruitments: 0
})

const recommendedRecruitments = ref([])
const recLoading = ref(false)

const fetchStats = async () => {
  try {
    const [myRec, myApp, myTeam, platform] = await Promise.all([
      getUserRecruitments(),
      getUserApplications(),
      getUserTeammates(),
      getRecruitments({ page: 1, size: 1 })
    ])
    stats.myRecruitments = myRec.data?.length || 0
    stats.myApplications = myApp.data?.length || 0
    stats.myTeammates = myTeam.data?.length || 0
    stats.platformRecruitments = platform.data?.total || 0
  } catch (e) {
    /* ignore — 错误已在拦截器处理 */
  }
}

const fetchRecommended = async () => {
  recLoading.value = true
  try {
    const res = await getRecommendedRecruitments()
    recommendedRecruitments.value = res.data || []
  } catch (e) {
    /* ignore */
  } finally {
    recLoading.value = false
  }
}

onMounted(() => {
  fetchStats()
  fetchRecommended()
})
</script>

<style scoped>
.home-container { max-width: 1000px; margin: 0 auto; padding-bottom: 40px; }

/* 欢迎区 */
.welcome-section { margin-bottom: 28px; }
.welcome-title { font-size: 26px; font-weight: 700; color: #1a1a1a; margin: 0 0 6px 0; }
.welcome-sub { color: #909399; font-size: 14px; margin: 0; }

/* 统计卡片 */
.stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 32px; }
@media (max-width: 768px) { .stats-grid { grid-template-columns: repeat(2, 1fr); } }
.stat-card {
  display: flex; align-items: center; gap: 14px;
  background: #fff; padding: 20px; border-radius: 10px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.05); transition: all 0.2s;
}
.stat-card:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
.stat-icon { width: 52px; height: 52px; border-radius: 12px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-value { font-size: 26px; font-weight: 700; color: #1a1a1a; line-height: 1.2; }
.stat-label { font-size: 13px; color: #909399; margin-top: 2px; }

/* 分区标题 */
.section { margin-bottom: 28px; }
.section-header { display: flex; align-items: baseline; gap: 12px; margin-bottom: 16px; }
.section-header h3 { font-size: 18px; font-weight: 600; color: #303133; margin: 0; }
.section-tip { font-size: 13px; color: #c0c4cc; }

/* 推荐招募卡片 */
.recommend-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; min-height: 80px; }
@media (max-width: 992px) { .recommend-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 576px) { .recommend-grid { grid-template-columns: 1fr; } }
.recommend-card {
  background: #fff; border-radius: 10px; padding: 18px;
  box-shadow: 0 1px 6px rgba(0,0,0,0.05); cursor: pointer;
  border: 1px solid transparent; transition: all 0.2s;
}
.recommend-card:hover { border-color: #1a73e8; box-shadow: 0 4px 14px rgba(26,115,232,0.12); transform: translateY(-2px); }
.rec-card-header { display: flex; gap: 12px; margin-bottom: 12px; align-items: flex-start; }
.rec-match { flex-shrink: 0; }
.rec-title-wrap h4 { margin: 0 0 4px 0; font-size: 15px; color: #303133; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.rec-category { font-size: 12px; color: #909399; display: flex; align-items: center; gap: 3px; }
.rec-tags { display: flex; flex-wrap: wrap; gap: 4px; margin-bottom: 12px; }
.rec-footer { display: flex; justify-content: space-between; font-size: 12px; color: #c0c4cc; }
.rec-footer span { display: flex; align-items: center; gap: 3px; }

/* 快捷入口 */
.quick-actions { display: flex; gap: 12px; flex-wrap: wrap; }
</style>
