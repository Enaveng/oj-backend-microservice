package com.oj.judgeservice.judge.codesandbox.impl;

import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import com.oj.common.common.ErrorCode;
import com.oj.common.exception.BusinessException;
import com.oj.judgeservice.judge.codesandbox.CodeSandBox;
import com.oj.model.dto.question.ExecuteCodeRequest;
import com.oj.model.dto.question.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

//后续实际使用的代码沙箱  远程代码沙箱
@Slf4j
public class RemoteCodeSandBox implements CodeSandBox {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        //调用我们自己开发的远程代码沙箱
        String url = "http://localhost:8090/executeCode";
        String jsonBody = JSONUtil.toJsonStr(executeCodeRequest);
        String response = null;  //得到String返回结果
        try {
            response = HttpUtil.createPost(url)
                    .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)   //key-value
                    .body(jsonBody)
                    .timeout(5000)  //设置连接超时时间
                    .execute()
                    .body();
        } catch (HttpException e) {
            log.info("代码沙箱接口异常");
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR);
        }
        log.info("得到的接口返回结果为:" + response);
        if (StringUtils.isBlank(response)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR, "remote CodeSandbox Error, message :" + response);
        }
        //将返回结果转换为对应的对象
        return JSONUtil.toBean(response, ExecuteCodeResponse.class);
    }
}
