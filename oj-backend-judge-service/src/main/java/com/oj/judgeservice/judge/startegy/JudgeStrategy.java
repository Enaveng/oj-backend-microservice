package com.oj.judgeservice.judge.startegy;


import com.oj.model.dto.question.JudgeInfo;

//将判题策略从JudgeServiceImpl当中抽离出来 不同的判题策略重写的内容不同
public interface JudgeStrategy {
    JudgeInfo doJudge(JudgeContext judgeContext);
}
