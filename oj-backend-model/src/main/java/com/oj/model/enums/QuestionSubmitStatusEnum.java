package com.oj.model.enums;


import com.oj.common.common.ErrorCode;
import com.oj.common.exception.BusinessException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题状态枚举类
 */
public enum QuestionSubmitStatusEnum {
    WAITING("等待中", 0),
    RUNNING("判题中", 1),
    SUCCEED("判题成功", 2),
    FAILED("判题失败", 3);


    private String text;
    private Integer value;

    QuestionSubmitStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public Integer getValue() {
        return value;
    }

    //得到所有的value值
    private static List<Integer> getValues() {
        return Arrays.stream(QuestionSubmitStatusEnum.values()).map(item -> item.value).collect(Collectors.toList());
    }

    //根据value得到对应的枚举值
    public static QuestionSubmitStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        for (QuestionSubmitStatusEnum questionSubmitStatusEnum : QuestionSubmitStatusEnum.values()) {
            if (questionSubmitStatusEnum.value.equals(value)) {
                return questionSubmitStatusEnum;
            }
        }
        return null;
    }
}
