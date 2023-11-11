package com.hdfk7.boot.starter.common.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public class RabbitUtil {
    private static final RabbitTemplate rabbitTemplate = SpringUtil.getBean("rabbitTemplate");

    public static void send(String exchange, String routingKey, Object message, MessagePostProcessor processor) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message instanceof String ? message : JSONUtil.toJsonStr(message), processor);
    }

    public static void send(String routingKey, Object message) {
        send("", routingKey, message, processor -> processor);
    }

    public static void send(String exchange, String routingKey, Object message) {
        send(exchange, routingKey, message, processor -> processor);
    }

    public static void send(String exchange, String routingKey, Object message, int delay) {
        send(exchange, routingKey, message, processor -> {
            processor.getMessageProperties().setDelay(delay);
            return processor;
        });
    }

    public static Object sendAndGet(String exchange, String routingKey, Object message, MessagePostProcessor processor) {
        return rabbitTemplate.convertSendAndReceive(exchange, routingKey, message instanceof String ? message : JSONUtil.toJsonStr(message), processor);
    }

    public static Object sendAndGet(String routingKey, Object message) {
        return sendAndGet("", routingKey, message, processor -> processor);
    }

    public static Object sendAndGet(String exchange, String routingKey, Object message) {
        return sendAndGet(exchange, routingKey, message, processor -> processor);
    }
}
