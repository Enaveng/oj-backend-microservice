package com.oj.judgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.oj.client.service.QuestionServiceFeignClient;
import com.oj.common.common.ErrorCode;
import com.oj.common.exception.BusinessException;
import com.oj.judgeservice.judge.codesandbox.CodeSandBoxFactory;
import com.oj.judgeservice.judge.codesandbox.CodeSandBox;
import com.oj.judgeservice.judge.codesandbox.CodeSandBoxProxy;
import com.oj.judgeservice.judge.startegy.JudgeContext;
import com.oj.model.dto.question.*;
import com.oj.model.entity.Question;
import com.oj.model.entity.QuestionSubmit;
import com.oj.model.enums.QuestionSubmitStatusEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Value("${codesandbox.type}")
    private String type;

    @Resource
    private QuestionServiceFeignClient questionServiceFeignClient;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        //根据传入题目的提交id 获取到对应的题目、题目提交信息(包括代码、编程语言等)
        QuestionSubmit questionSubmit = questionServiceFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交的题目不存在");
        }
        //通过提交的题目得到对应的题目id
        Long questionId = questionSubmit.getQuestionId();
        //根据id进行题目的查询
        Question question = questionServiceFeignClient.getQuestionById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目信息不存在");
        }
        //只有提交的题目的status字段为'等待中'才可以去执行代码沙箱
        if (!questionSubmit.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "题目正在判题中或已经判题完成");
        }
        //更新判题状态(题目提交表)为"判题中"，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionServiceFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新失败");
        }
        //调用代码沙箱得到执行结果
        CodeSandBox codeSandBox = CodeSandBoxFactory.createCodeSandBoxByType(type);
        CodeSandBoxProxy codeSandBoxProxy = new CodeSandBoxProxy(codeSandBox);
        String language = questionSubmit.getLanguage();
        String code = questionSubmit.getCode();
        //数据库保存的是json格式 需要将其转换为List
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest().builder()
                .code(code)
                .inputList(inputList)
                .language(language)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBoxProxy.executeCode(executeCodeRequest);
        List<String> outputList = executeCodeResponse.getOutputList();
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = new JudgeManager().doJudge(judgeContext);
        //更新状态
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        //将JudgeInfo对象转换为json对象
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        update = questionServiceFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionServiceFeignClient.getQuestionSubmitById(questionId);
        return questionSubmitResult;
    }
}
