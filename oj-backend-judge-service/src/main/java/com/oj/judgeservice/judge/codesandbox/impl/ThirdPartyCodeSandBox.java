package com.oj.judgeservice.judge.codesandbox.impl;

import com.oj.judgeservice.judge.codesandbox.CodeSandBox;
import com.oj.model.dto.question.ExecuteCodeRequest;
import com.oj.model.dto.question.ExecuteCodeResponse;

//第三方代码沙箱
public class ThirdPartyCodeSandBox implements CodeSandBox {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
