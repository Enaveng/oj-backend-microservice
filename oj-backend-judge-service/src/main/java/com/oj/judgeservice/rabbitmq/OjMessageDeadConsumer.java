package com.oj.judgeservice.rabbitmq;

import cn.hutool.json.JSONUtil;
import com.oj.common.common.ErrorCode;
import com.oj.common.config.RabbitMqConfig;
import com.oj.common.exception.BusinessException;
import com.oj.model.entity.QuestionSubmit;
import com.oj.model.enums.QuestionSubmitStatusEnum;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * 监听死信队列
 */
@Slf4j
@Component
public class OjMessageDeadConsumer {


    @RabbitListener(queues = RabbitMqConfig.OJ_DEAD_QUEUE_NAME)
    public void receiveDeadMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("接收死信队列的消息为:" + message);
        if (StringUtils.isBlank(message)) {
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息为空");
        }
        long questionSubmitId = Long.parseLong(message);
        //修改题目状态为失败
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        //将JudgeInfo对象转换为json对象
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
        // 确认消息
        channel.basicAck(deliveryTag, false);
    }
}
