package com.oj.model.dto.question;

import lombok.Data;


/**
 * 判题信息
 */
@Data
public class JudgeInfo {
    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 执行时间
     */
    private long time;

    /**
     * 题目执行消耗的内存(KB)
     */
    private long memory;

}
