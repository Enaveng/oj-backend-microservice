package com.oj.judgeservice.rabbitmq;


import com.oj.common.common.ErrorCode;
import com.oj.common.config.RabbitMqConfig;
import com.oj.common.exception.BusinessException;
import com.oj.judgeservice.judge.JudgeService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;


/**
 * 监听正常得到队列消息的消费者方法
 */
@Component
@Slf4j
public class OjMessageConsumer {

    @Resource
    private JudgeService judgeService;


    @RabbitListener(queues = RabbitMqConfig.OJ_QUEUE_NAME)
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        log.info("接收到的消息为:" + message);
        //校验
        if (!StringUtils.isNotBlank(message)) {
            //表示接收的消息参数为空 将该消息拒绝
            channel.basicNack(deliveryTag, false, false);  //表示单个消息拒绝 不将拒绝的消息放回队列当中
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接受到的消息为空");
        }
        //得到消息传递的chartId(传递的是string类型)  判题服务当中我们只需要向消息队列当中发送用户上传的题目id即可
        long questionId = Long.parseLong(message);
        judgeService.doJudge(questionId);
    }
}