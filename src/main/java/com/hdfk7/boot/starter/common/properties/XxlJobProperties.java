package com.hdfk7.boot.starter.common.properties;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "xxl.job")
@ConditionalOnClass(value = {XxlJobSpringExecutor.class})
public class XxlJobProperties {
    private String adminAddresses;

    private String accessToken;

    private String appName;

    private String address;

    private String ip;

    private int port;

    private String logPath;

    private int logRetentionDays;
}
