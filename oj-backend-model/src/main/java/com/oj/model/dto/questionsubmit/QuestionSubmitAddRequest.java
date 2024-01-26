package com.oj.model.dto.questionsubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目创建请求
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 提交代码时所选择的语言
     */
    private String language;

    /**
     * 用户提交源代码
     */
    private String code;

    /**
     * 题目id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
