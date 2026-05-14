/**
 * 认证相关 API
 */
import request from './request'

// 用户登录
export const login = (data) => request.post('/auth/login', data)

// 用户注册
export const register = (data) => request.post('/auth/register', data)
