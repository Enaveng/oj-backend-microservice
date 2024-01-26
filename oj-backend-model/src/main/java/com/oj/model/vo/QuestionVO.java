package com.oj.model.vo;

import cn.hutool.json.JSONUtil;
import com.oj.model.dto.question.JudgeConfig;
import com.oj.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目封装类 是后端向前端返回数据的实体类
 */
@Data
public class QuestionVO implements Serializable {

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
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 判题配置(json对象)
     */
    private JudgeConfig judgeConfig;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    //将封装类转化为对象
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> voTags = questionVO.getTags();
        if (voTags != null) {
            question.setTags(JSONUtil.toJsonStr(voTags));
        }
        //转换判题配置
        JudgeConfig voJudgeConfig = questionVO.getJudgeConfig();
        if (voJudgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        return question;
    }


    //将对象转换为封装类
    public static QuestionVO objToVo(Question question) {
        //校验
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        String questionTags = question.getTags();
        if (questionTags != null) {
            questionVO.setTags(JSONUtil.toList(questionTags, String.class));
        }
        String questionJudgeConfig = question.getJudgeConfig();
        if (questionJudgeConfig != null) {
            questionVO.setJudgeConfig(JSONUtil.toBean(questionJudgeConfig, JudgeConfig.class));
        }
        return questionVO;
    }


    private static final long serialVersionUID = 1L;
}
