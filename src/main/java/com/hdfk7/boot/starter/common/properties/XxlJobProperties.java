package com.hdfk7.boot.starter.common.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "xxl.job")
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
