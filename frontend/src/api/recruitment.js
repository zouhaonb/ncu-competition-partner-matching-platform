/**
 * 招募相关 API
 */
import request from './request'

// 创建新的招募
export const createRecruitment = (data) => request.post('/recruitments', data)

// 获取招募列表（支持筛选）
export const getRecruitments = (params) => request.get('/recruitments', { params })

// 获取招募详情
export const getRecruitmentDetail = (id) => request.get(`/recruitments/${id}`)

// 关闭招募
export const closeRecruitment = (id) => request.put(`/recruitments/${id}/close`)

// 获取推荐队友
export const getRecommendations = (id) => request.get(`/recruitments/${id}/recommendations`)

// 申请加入招募
export const applyRecruitment = (id, data) => request.post(`/recruitments/${id}/apply`, data)

// 获取申请列表（招募者查看）
export const getApplications = (id) => request.get(`/recruitments/${id}/applications`)

// 处理申请（同意/拒绝）
export const handleApplication = (id, data) => request.put(`/applications/${id}/handle`, data)

// 推荐匹配的招募（首页）
export const getRecommendedRecruitments = () => request.get('/recruitments/recommended')
