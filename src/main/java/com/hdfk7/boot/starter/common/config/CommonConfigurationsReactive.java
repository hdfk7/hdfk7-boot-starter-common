package com.hdfk7.boot.starter.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdfk7.proto.base.json.JacksonObjectMapperInstance;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class CommonConfigurationsReactive {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonObjectMapperInstance.getMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters messageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        List<HttpMessageConverter<?>> list = converters.orderedStream().collect(Collectors.toList());
        list.removeIf(o -> o instanceof MappingJackson2HttpMessageConverter);
        list.add(new MappingJackson2HttpMessageConverter(JacksonObjectMapperInstance.getMapper()));
        return new HttpMessageConverters(list);
    }
}
