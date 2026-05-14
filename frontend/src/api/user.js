/**
 * 用户相关 API
 */
import request from './request'

// 获取当前用户信息
export const getUserProfile = () => request.get('/user/me')

// 更新用户个人信息
export const updateUserProfile = (data) => request.put('/user/me', data)

// 获取我的招募列表
export const getUserRecruitments = () => request.get('/user/recruitments')

// 获取我的申请列表
export const getUserApplications = () => request.get('/user/applications')

// 获取我的队友列表
export const getUserTeammates = () => request.get('/user/teammates')
