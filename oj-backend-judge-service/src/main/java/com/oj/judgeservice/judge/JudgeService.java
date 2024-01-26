package com.oj.judgeservice.judge;


import com.oj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题服务
 */
@Service
public interface JudgeService {
    //判题
    QuestionSubmit doJudge(long questionSubmitId);
}
