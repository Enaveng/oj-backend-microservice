package com.oj.model.dto.question;


import com.oj.common.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 判题用例
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置(前端传递的是json对象 我们新建实体类进行封装)
     */
    private JudgeConfig judgeConfig;
    /**
     * 创建用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}