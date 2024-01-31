package com.oj.common.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class RabbitMqConfig {
    //普通交换机
    public static final String OJ_EXCHANGE_NAME = "OjExchange";
    //普通队列
    public static final String OJ_QUEUE_NAME = "OjQueue";
    //死信交换机
    public static final String OJ_DEAD_EXCHANGE_NAME = "DeadExchange";
    //死信队列
    public static final String OJ_DEAD_QUEUE_NAME = "DeadQueue";
    //routingKey
    public static final String OJ_ROUTING_KEY = "OjKey";
    //死信routingKey
    public static final String OJ_DEAD_ROUTING_KEY = "DeadOjKey";


    //创建普通交换机
    @Bean
    public DirectExchange ojExchange() {
        return new DirectExchange(OJ_EXCHANGE_NAME, true, false);
    }

    //创建死信交换机
    @Bean
    public DirectExchange ojDeadExchange() {
        return new DirectExchange(OJ_DEAD_EXCHANGE_NAME, true, false);
    }

    //创建普通队列  为用户提交题目的请求添加过期时间
    @Bean
    public Queue ojQueue() {
        Map<String, Object> arguments = new HashMap<>();
        //为该队列添加死信交换机和死信队列
        //设置死信交换机
        arguments.put("x-dead-letter-exchange", OJ_DEAD_EXCHANGE_NAME);
        //设置死信RoutingKey
        arguments.put("x-dead-letter-routing-key", OJ_DEAD_ROUTING_KEY);
        //设置TTL 60s 单位是ms 表示队列消息的过期时间为60s
        arguments.put("x-message-ttl", 60000);
        return QueueBuilder.durable(OJ_QUEUE_NAME).withArguments(arguments).build();
    }

    //声明死信队列
    @Bean
    public Queue ojDeadQueue() {
        return QueueBuilder.durable(OJ_DEAD_QUEUE_NAME).build();
    }

    //绑定交换机与队列
    @Bean
    public Binding ojBinding() {
        return BindingBuilder.bind(ojQueue()).to(ojExchange()).with(OJ_ROUTING_KEY);
    }

    //绑定死信交换机与死信队列
    @Bean
    public Binding ojDeadBinding() {
        return BindingBuilder.bind(ojDeadQueue()).to(ojDeadExchange()).with(OJ_ROUTING_KEY);
    }

}
