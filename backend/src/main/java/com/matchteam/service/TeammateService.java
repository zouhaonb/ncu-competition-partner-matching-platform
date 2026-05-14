package com.matchteam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matchteam.dto.UserProfileResponse;
import com.matchteam.entity.Application;
import com.matchteam.entity.Recruitment;
import com.matchteam.entity.User;
import com.matchteam.mapper.ApplicationMapper;
import com.matchteam.mapper.RecruitmentMapper;
import com.matchteam.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 队友服务 - 管理已确认的队友关系
 * 当申请被接受后，申请者和发布者互为队友，可互见联系方式
 */
@Service
@RequiredArgsConstructor
public class TeammateService {

    private final ApplicationMapper applicationMapper;
    private final RecruitmentMapper recruitmentMapper;
    private final UserMapper userMapper;

    /**
     * 获取当前用户的队友列表
     * 队友关系来源：
     * 1. 用户作为发布者，接受了某人的申请 → 该申请者成为队友
     * 2. 用户的申请被某发布者接受 → 该发布者成为队友
     */
    public List<UserProfileResponse> getTeammates(Long userId) {
        Set<Long> teammateIds = new HashSet<>();

        // 获取所有已接受的申请记录
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getStatus, "ACCEPTED");
        List<Application> allAccepted = applicationMapper.selectList(wrapper);

        for (Application app : allAccepted) {
            Recruitment recruitment = recruitmentMapper.selectById(app.getRecruitmentId());
            if (recruitment == null) continue;

            // 情况1: 当前用户是申请者 → 发布者是队友
            if (app.getApplicantId().equals(userId)) {
                if (!recruitment.getPublisherId().equals(userId)) {
                    teammateIds.add(recruitment.getPublisherId());
                }
            }

            // 情况2: 当前用户是发布者 → 申请者是队友
            if (recruitment.getPublisherId().equals(userId)) {
                if (!app.getApplicantId().equals(userId)) {
                    teammateIds.add(app.getApplicantId());
                }
            }
        }

        // 构建队友信息列表（已确认队友关系，可显示联系方式）
        List<UserProfileResponse> teammates = new ArrayList<>();
        for (Long teammateId : teammateIds) {
            User user = userMapper.selectById(teammateId);
            if (user != null) {
                UserProfileResponse resp = new UserProfileResponse();
                resp.setId(user.getId());
                resp.setStudentId(user.getStudentId());
                resp.setName(user.getName());
                resp.setPhone(user.getPhone());   // 队友可互见联系方式
                resp.setQq(user.getQq());
                resp.setIntro(user.getIntro());
                resp.setCreateTime(user.getCreateTime());
                teammates.add(resp);
            }
        }
        return teammates;
    }
}
