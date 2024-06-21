package com.hdfk7.boot.starter.common.aspect;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.hdfk7.proto.base.annotation.ResubmitCheck;
import com.hdfk7.proto.base.exception.ResubmitException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class ResubmitCheckAspect {
    protected static final String TOKEN = "TOKEN";

    protected void doAdvice(JoinPoint joinPoint, ResubmitCheck formRepeatSubmitValidation) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra == null) {
            return;
        }
        HttpServletRequest request = sra.getRequest();
        String authorization = request.getHeader(TOKEN);
        if (StringUtils.isEmpty(authorization)) {
            authorization = request.getParameter(TOKEN);
        }
        if (StringUtils.isEmpty(authorization)) {
            return;
        }
        String methodType = request.getMethod();
        if (Arrays.stream(formRepeatSubmitValidation.methods()).noneMatch(o -> o.equalsIgnoreCase(methodType))) {
            return;
        }
        String methodName = joinPoint.getSignature().getName();
        Map<String, String[]> map = request.getParameterMap();
        StringBuilder sb = new StringBuilder();
        sb.append(request.getRequestURL()).append(methodName).append(authorization);
        map.forEach((k, v) -> {
            sb.append(k);
            if (v != null) {
                sb.append(String.join("", v));
            }
        });
        log.debug("REQ: ttl {} {}", formRepeatSubmitValidation.ttl(), sb.toString());
        checkResubmit(sb.toString(), formRepeatSubmitValidation.ttl());
    }

    protected void checkResubmit(String key, int ttl) {
        String md5Key = SecureUtil.md5(key);
        RedissonClient redissonClient = SpringUtil.getBean(RedissonClient.class);
        RLock lock = redissonClient.getLock("resubmit_check:" + SpringUtil.getApplicationName() + ":" + md5Key);
        try {
            boolean tryLock = lock.tryLock(0, ttl, TimeUnit.SECONDS);
            if (!tryLock) {
                throw new ResubmitException();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
