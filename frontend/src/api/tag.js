/**
 * 标签相关 API
 */
import request from './request'

// 获取所有可用标签
export const getAllTags = () => request.get('/tags')

// 获取当前用户的标签
export const getUserTags = () => request.get('/user/tags')

// 为用户添加标签
export const addUserTag = (data) => request.post('/user/tags', data)

// 删除用户标签
export const deleteUserTag = (id) => request.delete(`/user/tags/${id}`)
