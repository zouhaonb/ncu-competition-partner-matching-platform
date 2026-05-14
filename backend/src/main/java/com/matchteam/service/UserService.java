package com.matchteam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matchteam.common.BusinessException;
import com.matchteam.common.JwtUtils;
import com.matchteam.dto.*;
import com.matchteam.entity.User;
import com.matchteam.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务 - 处理注册、登录、个人信息管理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * 用户注册
     * 检查学号是否已被注册，加密密码后保存
     */
    public void register(RegisterRequest request) {
        // 检查学号是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStudentId, request.getStudentId());
        Long count = userMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该学号已被注册");
        }

        // 创建用户，密码使用BCrypt加密
        User user = new User();
        user.setStudentId(request.getStudentId());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setQq(request.getQq());

        userMapper.insert(user);
        log.info("新用户注册成功: {}", request.getStudentId());
    }

    /**
     * 用户登录
     * 验证学号和密码，返回JWT令牌
     */
    public LoginResponse login(LoginRequest request) {
        // 根据学号查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStudentId, request.getStudentId());
        User user = userMapper.selectOne(wrapper);

        if (user == null) {
            throw new BusinessException("学号或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("学号或密码错误");
        }

        // 生成JWT令牌（包含角色信息）
        String token = jwtUtils.generateToken(user.getId(), user.getRole());
        log.info("用户登录成功: {}", user.getStudentId());

        return new LoginResponse(token, user.getId(), user.getStudentId(), user.getName(), user.getRole());
    }

    /**
     * 获取当前用户个人信息
     */
    public UserProfileResponse getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setStudentId(user.getStudentId());
        response.setName(user.getName());
        response.setPhone(user.getPhone());
        response.setQq(user.getQq());
        response.setIntro(user.getIntro());
        response.setCreateTime(user.getCreateTime());
        return response;
    }

    /**
     * 更新个人信息
     */
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setQq(request.getQq());
        user.setIntro(request.getIntro());
        userMapper.updateById(user);
        log.info("用户信息更新成功: {}", userId);
    }
}
