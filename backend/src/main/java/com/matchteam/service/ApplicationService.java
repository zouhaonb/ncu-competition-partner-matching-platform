package com.matchteam.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.matchteam.common.BusinessException;
import com.matchteam.dto.ApplicationResponse;
import com.matchteam.entity.Application;
import com.matchteam.entity.Recruitment;
import com.matchteam.entity.User;
import com.matchteam.mapper.ApplicationMapper;
import com.matchteam.mapper.RecruitmentMapper;
import com.matchteam.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 申请服务 - 处理队友申请、审核等操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationMapper applicationMapper;
    private final RecruitmentMapper recruitmentMapper;
    private final UserMapper userMapper;

    /**
     * 申请加入招募
     */
    public void apply(Long recruitmentId, Long applicantId, String reason) {
        // 检查招募是否存在且状态为OPEN
        Recruitment recruitment = recruitmentMapper.selectById(recruitmentId);
        if (recruitment == null) {
            throw new BusinessException("招募信息不存在");
        }
        if ("CLOSED".equals(recruitment.getStatus())) {
            throw new BusinessException("招募已关闭，无法申请");
        }
        // 不能申请自己的招募
        if (recruitment.getPublisherId().equals(applicantId)) {
            throw new BusinessException("不能申请自己发布的招募");
        }

        // 检查是否已申请
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getRecruitmentId, recruitmentId)
               .eq(Application::getApplicantId, applicantId);
        if (applicationMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("您已申请过该招募，请勿重复申请");
        }

        Application application = new Application();
        application.setRecruitmentId(recruitmentId);
        application.setApplicantId(applicantId);
        application.setReason(reason);
        application.setStatus("PENDING");
        applicationMapper.insert(application);
        log.info("用户{}申请加入招募{}", applicantId, recruitmentId);
    }

    /**
     * 获取招募的所有申请（仅发布者可查看）
     * 被接受的申请会显示联系方式
     */
    public List<ApplicationResponse> getApplications(Long recruitmentId, Long publisherId) {
        Recruitment recruitment = recruitmentMapper.selectById(recruitmentId);
        if (recruitment == null) {
            throw new BusinessException("招募信息不存在");
        }
        if (!recruitment.getPublisherId().equals(publisherId)) {
            throw new BusinessException("只有发布者才能查看申请列表");
        }

        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getRecruitmentId, recruitmentId)
               .orderByDesc(Application::getApplyTime);
        List<Application> applications = applicationMapper.selectList(wrapper);

        return applications.stream().map(app -> {
            User applicant = userMapper.selectById(app.getApplicantId());
            ApplicationResponse resp = new ApplicationResponse();
            resp.setId(app.getId());
            resp.setRecruitmentId(app.getRecruitmentId());
            resp.setApplicantId(app.getApplicantId());
            resp.setApplicantName(applicant != null ? applicant.getName() : "未知");
            resp.setReason(app.getReason());
            resp.setStatus(app.getStatus());
            resp.setApplyTime(app.getApplyTime());

            // 只有被接受的申请才显示联系方式
            if ("ACCEPTED".equals(app.getStatus()) && applicant != null) {
                resp.setApplicantPhone(applicant.getPhone());
                resp.setApplicantQq(applicant.getQq());
            }
            return resp;
        }).collect(Collectors.toList());
    }

    /**
     * 处理申请（接受或拒绝）
     * 仅招募发布者可操作
     */
    @Transactional
    public void handleApplication(Long applicationId, Long publisherId, String status) {
        Application application = applicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException("申请记录不存在");
        }

        // 验证操作者是发布者
        Recruitment recruitment = recruitmentMapper.selectById(application.getRecruitmentId());
        if (recruitment == null || !recruitment.getPublisherId().equals(publisherId)) {
            throw new BusinessException("只有发布者才能处理申请");
        }

        if (!"PENDING".equals(application.getStatus())) {
            throw new BusinessException("该申请已被处理");
        }

        application.setStatus(status);
        applicationMapper.updateById(application);
        log.info("申请{}已被{}: applicationId={}", status, applicationId);
    }

    /**
     * 获取当前用户的所有申请记录
     */
    public List<ApplicationResponse> getUserApplications(Long userId) {
        LambdaQueryWrapper<Application> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Application::getApplicantId, userId)
               .orderByDesc(Application::getApplyTime);
        List<Application> applications = applicationMapper.selectList(wrapper);

        return applications.stream().map(app -> {
            Recruitment recruitment = recruitmentMapper.selectById(app.getRecruitmentId());
            ApplicationResponse resp = new ApplicationResponse();
            resp.setId(app.getId());
            resp.setRecruitmentId(app.getRecruitmentId());
            resp.setRecruitmentTitle(recruitment != null ? recruitment.getTitle() : "招募已删除");
            resp.setReason(app.getReason());
            resp.setStatus(app.getStatus());
            resp.setApplyTime(app.getApplyTime());
            return resp;
        }).collect(Collectors.toList());
    }
}
