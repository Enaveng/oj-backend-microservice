package com.oj.judgeservice.judge.startegy;


import com.oj.model.dto.question.JudgeCase;
import com.oj.model.dto.question.JudgeInfo;
import com.oj.model.entity.Question;
import com.oj.model.entity.QuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文(用于在策略中传递的参数)
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
