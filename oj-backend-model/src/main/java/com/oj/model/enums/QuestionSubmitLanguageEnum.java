package com.oj.model.enums;



import com.oj.common.common.ErrorCode;
import com.oj.common.exception.BusinessException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提交题目时选择的编程语言枚举类
 */
public enum QuestionSubmitLanguageEnum {

    JAVA("java", "java"),
    CPLUSPLUS("c++", "c++"),
    PYTHON("python", "python"),
    GOLANG("golang", "golang");

    private final String text;
    private final String value;

    QuestionSubmitLanguageEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }


    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

    //得到全部的value
    public static List<String> getValues() {
        return Arrays.stream(QuestionSubmitLanguageEnum.values()).map(item -> item.value).collect(Collectors.toList());
    }

    //根据value得到枚举值
    public static QuestionSubmitLanguageEnum getEnumByValue(String value) {
        if (value == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //遍历得到所有的value
        for (QuestionSubmitLanguageEnum questionSubmitLanguageEnum : QuestionSubmitLanguageEnum.values()) {
            if (questionSubmitLanguageEnum.value.equals(value)) {
                return questionSubmitLanguageEnum;
            }
        }
        return null;
    }
}
