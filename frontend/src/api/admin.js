/**
 * 管理员相关 API
 */
import request from './request'

// 获取所有竞赛类别
export const getAdminCategories = () => request.get('/admin/categories')

// 添加竞赛类别
export const addCategory = (data) => request.post('/admin/categories', data)

// 删除竞赛类别
export const deleteCategory = (id) => request.delete(`/admin/categories/${id}`)

// 获取所有用户列表
export const getAdminUsers = () => request.get('/admin/users')

// 获取所有招募列表
export const getAdminRecruitments = () => request.get('/admin/recruitments')

// 删除用户
export const deleteUser = (id) => request.delete(`/admin/users/${id}`)

// 关闭招募（管理员操作）
export const closeRecruitmentAdmin = (id) => request.put(`/admin/recruitments/${id}/close`)

// 删除招募
export const deleteRecruitment = (id) => request.delete(`/admin/recruitments/${id}`)
