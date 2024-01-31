package com.oj.questionservice.rabbitmq;


import com.oj.common.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class OjMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param message
     */
    public void sendMessage(String message) {
        //创建发送消息回调接口返回对象
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId("id" + message);
        rabbitTemplate.convertAndSend(RabbitMqConfig.OJ_EXCHANGE_NAME,
                RabbitMqConfig.OJ_ROUTING_KEY, message, correlationData);
        log.info("发送的消息为: " + message);
    }
}
