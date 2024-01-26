package com.oj.model.dto.question;

import lombok.Data;


/**
 * 封装的是前端传递的判题用例 前端传递的是json数据 方便接收我们定义一个实体类
 */
@Data
public class JudgeCase {
    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出用例
     */
    private String output;

}
