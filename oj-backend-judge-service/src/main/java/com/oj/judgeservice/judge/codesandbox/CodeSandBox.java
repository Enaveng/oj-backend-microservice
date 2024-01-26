package com.oj.judgeservice.judge.codesandbox;


import com.oj.model.dto.question.ExecuteCodeRequest;
import com.oj.model.dto.question.ExecuteCodeResponse;

/**
 * 代码沙箱职责:
 * 得到一组输入用例 将代码编译运行之后返回给判题服务模块
 */

//定义代码沙箱的接口
public interface CodeSandBox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
