<!-- 招募广场页面 - 卡片列表展示、搜索筛选、发布招募 -->
<template>
  <div class="square-container">
    <!-- 顶部操作栏 -->
    <div class="square-toolbar">
      <div class="toolbar-left">
        <el-input v-model="keyword" placeholder="搜索招募标题..." clearable :prefix-icon="Search" style="width: 280px" @keyup.enter="fetchList" @clear="fetchList" />
        <el-select v-model="categoryId" placeholder="竞赛类别" clearable style="width: 180px; margin-left: 12px" @change="fetchList">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
        <el-button type="primary" :icon="Search" style="margin-left: 12px" @click="fetchList">搜索</el-button>
      </div>
      <el-button type="primary" :icon="Plus" @click="showCreateDialog = true">发布招募</el-button>
    </div>

    <!-- 招募卡片网格 -->
    <div class="card-grid" v-loading="loading">
      <div v-for="rec in list" :key="rec.id" class="recruitment-card" @click="$router.push(`/recruitment/${rec.id}`)">
        <div class="card-header">
          <h3 class="card-title">{{ rec.title }}</h3>
          <el-tag :type="rec.status === 'OPEN' ? 'success' : 'info'" size="small">{{ rec.status === 'OPEN' ? '招募中' : '已关闭' }}</el-tag>
        </div>
        <div class="card-body">
          <div class="card-info">
            <span><el-icon><Collection /></el-icon> {{ rec.categoryName }}</span>
            <span><el-icon><UserFilled /></el-icon> 需{{ rec.requiredNumber }}人</span>
          </div>
          <div class="card-publisher">
            <el-icon><Avatar /></el-icon> {{ rec.publisherName }}
          </div>
        </div>
        <div class="card-footer">
          <span class="card-time">{{ rec.createTime?.substring(0, 10) }}</span>
        </div>
      </div>
      <el-empty v-if="!loading && list.length === 0" description="暂无招募信息" style="grid-column: 1/-1" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination background layout="prev, pager, next" :total="total" :page-size="pageSize" v-model:current-page="currentPage" @current-change="fetchList" />
    </div>

    <!-- 发布招募弹窗 -->
    <el-dialog v-model="showCreateDialog" title="发布竞赛招募" width="560px" :close-on-click-modal="false">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="100px">
        <el-form-item label="竞赛名称" prop="title">
          <el-autocomplete v-model="createForm.title" placeholder="输入竞赛名称或选择" :fetch-suggestions="queryPresetTitles" clearable />
        </el-form-item>
        <el-form-item label="竞赛类别" prop="categoryId">
          <el-select v-model="createForm.categoryId" placeholder="请选择" style="width: 100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="需要人数" prop="requiredNumber">
          <el-input-number v-model="createForm.requiredNumber" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="所需技能" prop="requiredTagIds">
          <el-select v-model="createForm.requiredTagIds" multiple placeholder="选择所需技能标签" style="width: 100%">
            <el-option v-for="tag in allTags" :key="tag.id" :label="tag.name" :value="tag.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="招募描述" prop="description">
          <el-input v-model="createForm.description" type="textarea" :rows="4" placeholder="描述竞赛要求、团队期望等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" :loading="createLoading" @click="handleCreate">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Plus, Collection, UserFilled, Avatar } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getRecruitments, createRecruitment } from '../api/recruitment'
import { getAllTags } from '../api/tag'
import request from '../api/request'

const keyword = ref('')
const categoryId = ref(null)
const currentPage = ref(1)
const pageSize = 12
const loading = ref(false)
const list = ref([])
const total = ref(0)
const categories = ref([])
const allTags = ref([])
const showCreateDialog = ref(false)
const createLoading = ref(false)
const createFormRef = ref(null)

const createForm = reactive({
  title: '',
  categoryId: null,
  requiredNumber: 3,
  description: '',
  requiredTagIds: []
})

const createRules = {
  title: [{ required: true, message: '请输入竞赛名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择竞赛类别', trigger: 'change' }],
  requiredNumber: [{ required: true, message: '请设置需要人数', trigger: 'blur' }]
}

const presetTitles = [
  '数学建模美赛队友招募', '全国大学生数学建模竞赛', '挑战杯创业计划赛',
  '蓝桥杯程序设计竞赛', 'ACM-ICPC队友招募', '电子设计大赛',
  '机械创新设计大赛', '互联网+创新创业大赛', '英语竞赛队友'
]

const queryPresetTitles = (queryString, cb) => {
  const results = queryString
    ? presetTitles.filter(t => t.includes(queryString)).map(t => ({ value: t }))
    : presetTitles.map(t => ({ value: t }))
  cb(results)
}

const fetchList = async () => {
  loading.value = true
  try {
    const res = await getRecruitments({
      keyword: keyword.value || undefined,
      categoryId: categoryId.value || undefined,
      page: currentPage.value,
      size: pageSize
    })
    list.value = res.data.records
    total.value = res.data.total
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await request.get('/admin/categories')
    categories.value = res.data
  } catch (e) { /* ignore */ }
}

const fetchTags = async () => {
  try {
    const res = await getAllTags()
    allTags.value = res.data
  } catch (e) { /* ignore */ }
}

const handleCreate = async () => {
  const valid = await createFormRef.value.validate().catch(() => false)
  if (!valid) return
  createLoading.value = true
  try {
    await createRecruitment(createForm)
    ElMessage.success('招募发布成功')
    showCreateDialog.value = false
    // 重置表单
    Object.assign(createForm, { title: '', categoryId: null, requiredNumber: 3, description: '', requiredTagIds: [] })
    fetchList()
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    createLoading.value = false
  }
}

onMounted(() => {
  fetchList()
  fetchCategories()
  fetchTags()
})
</script>

<style scoped>
.square-container { padding: 0 0 40px 0; }
.square-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  margin-bottom: 20px;
}
.toolbar-left { display: flex; align-items: center; flex-wrap: wrap; gap: 0; }
.card-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
@media (max-width: 992px) { .card-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 576px) { .card-grid { grid-template-columns: 1fr; } }
.recruitment-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}
.recruitment-card:hover {
  box-shadow: 0 4px 16px rgba(26,115,232,0.12);
  border-color: #1a73e8;
  transform: translateY(-2px);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}
.card-title {
  font-size: 16px;
  color: #303133;
  margin: 0;
  flex: 1;
  margin-right: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.card-info {
  display: flex;
  gap: 16px;
  color: #606266;
  font-size: 13px;
  margin-bottom: 8px;
}
.card-info span { display: flex; align-items: center; gap: 4px; }
.card-publisher { color: #909399; font-size: 13px; display: flex; align-items: center; gap: 4px; }
.card-footer { margin-top: 12px; padding-top: 12px; border-top: 1px solid #ebeef5; }
.card-time { color: #c0c4cc; font-size: 12px; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 24px; }
</style>
