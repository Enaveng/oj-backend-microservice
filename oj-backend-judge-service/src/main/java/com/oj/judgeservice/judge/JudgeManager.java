package com.oj.judgeservice.judge;


import com.oj.judgeservice.judge.startegy.DefaultJudgeStrategy;
import com.oj.judgeservice.judge.startegy.JavaJudgeStrategy;
import com.oj.judgeservice.judge.startegy.JudgeContext;
import com.oj.judgeservice.judge.startegy.JudgeStrategy;
import com.oj.model.dto.question.JudgeInfo;
import com.oj.model.entity.QuestionSubmit;
import com.oj.model.enums.QuestionSubmitLanguageEnum;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JudgeManager {
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        //判断提交题目的语言是什么
        String language = questionSubmit.getLanguage();
        if (language.equals(QuestionSubmitLanguageEnum.JAVA.getValue())) {
            judgeStrategy = new JavaJudgeStrategy();
        }
        log.info("得到的judgeInfo为" + judgeStrategy.doJudge(judgeContext));
        return judgeStrategy.doJudge(judgeContext);
    }
}
