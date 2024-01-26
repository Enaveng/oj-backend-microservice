package com.oj.questionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oj.common.common.ErrorCode;
import com.oj.common.constant.CommonConstant;
import com.oj.common.exception.BusinessException;
import com.oj.common.utils.SqlUtils;
import com.oj.judgeservice.judge.JudgeService;
import com.oj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.oj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.oj.model.entity.QuestionSubmit;
import com.oj.model.entity.User;
import com.oj.model.enums.QuestionSubmitLanguageEnum;
import com.oj.model.enums.QuestionSubmitStatusEnum;
import com.oj.model.vo.QuestionSubmitVO;
import com.oj.questionservice.mapper.QuestionSubmitMapper;
import com.oj.questionservice.service.QuestionSubmitService;
import com.oj.user.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author 86158
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-11-24 10:08:07
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {


    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    //返回题目的提交id
    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum questionSubmitLanguage = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (questionSubmitLanguage == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "提交的编程语言类型错误");
        }
        String code = questionSubmitAddRequest.getCode();
        Long questionId = questionSubmitAddRequest.getQuestionId();
        Long id = loginUser.getId();
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(code);
        questionSubmit.setJudgeInfo("{}");
        //设置初始的判题状态 (等待中)
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(id);
        //保存数据
        boolean b = this.save(questionSubmit);
        if (!b) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据保存失败");
        }
        Long submitId = questionSubmit.getId();
        //执行判题服务(异步执行)  异步执行时调用接口到达超时时间不会报错
        CompletableFuture.runAsync(() -> {
            judgeService.doJudge(submitId);
        });

//        judgeService.doJudge(submitId);

        return submitId;
    }


    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userService.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

}




