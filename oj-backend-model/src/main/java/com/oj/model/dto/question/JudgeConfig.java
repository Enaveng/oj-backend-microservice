package com.oj.model.dto.question;


import lombok.Data;

/**
 * 判题配置
 * 比如写题时存在时间限制、内存限制等
 */

@Data
public class JudgeConfig {

    /**
     * 时间限制
     */
    private long timeLimit;

    /**
     * 内存限制
     */
    private long memoryLimit;

    /**
     * 堆栈限制
     */
    private long stackLimit;

}
