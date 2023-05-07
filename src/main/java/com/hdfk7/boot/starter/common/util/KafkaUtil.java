package com.hdfk7.boot.starter.common.util;

import cn.hutool.extra.spring.SpringUtil;
import com.hdfk7.proto.base.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

@Slf4j
public class KafkaUtil {
    private static final KafkaTemplate<String, String> kafkaTemplate = SpringUtil.getBean("kafkaTemplate");

    public static void send(String channel, Object obj, BiConsumer<SendResult<String, String>, Throwable> callback) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(channel, obj instanceof String ? (String) obj : JsonUtil.toJsonStr(obj));
        if (callback != null) {
            future.whenComplete(callback);
        }
    }

    public static void send(String channel, Object obj) {
        send(channel, obj, null);
    }

    public static SendResult<String, String> sendSync(String channel, Object obj) {
        try {
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(channel, obj instanceof String ? (String) obj : JsonUtil.toJsonStr(obj));
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
