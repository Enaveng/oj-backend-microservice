package com.oj.judgeservice.judge.startegy;

import cn.hutool.json.JSONUtil;
import com.oj.model.dto.question.JudgeCase;
import com.oj.model.dto.question.JudgeConfig;
import com.oj.model.dto.question.JudgeInfo;
import com.oj.model.entity.Question;
import com.oj.model.enums.JudgeInfoMessageEnum;


import java.util.List;

//默认的判题策略
public class DefaultJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();
        //根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        //判断输出个数与输入个数是否相同
        if (outputList.size() != inputList.size()) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return null;
        }
        //依次判断代码沙箱的每个输出是否与预期输出相同
        for (int i = 0; i < judgeCaseList.size(); i++) {
            //得到每一个判题用例对象
            JudgeCase judgeCase = judgeCaseList.get(i);
            if (judgeCase.getOutput().equals(outputList.get(i))) {
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return null;
            }
        }
        //判断其他运行限制条件
        long memory = judgeInfo.getMemory();
        long time = judgeInfo.getTime();
        //得到题目当中的判题配置
        String judgeConfig = question.getJudgeConfig();
        //转换为java对象
        JudgeConfig config = JSONUtil.toBean(judgeConfig, JudgeConfig.class);
        long memoryLimit = config.getMemoryLimit();
        long timeLimit = config.getTimeLimit();
        if (memory > memoryLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return null;
        }
        if (time > timeLimit) {
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return null;
        }
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
