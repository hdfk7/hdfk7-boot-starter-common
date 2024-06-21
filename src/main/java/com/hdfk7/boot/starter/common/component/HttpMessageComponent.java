package com.hdfk7.boot.starter.common.component;

import com.hdfk7.proto.base.json.JacksonObjectMapperInstance;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ConditionalOnClass({HttpMessageConverter.class, HttpMessageConverters.class})
public class HttpMessageComponent {

    @Bean
    @ConditionalOnMissingBean
    public HttpMessageConverters httpMessageConverters(ObjectProvider<HttpMessageConverter<?>> converters) {
        List<HttpMessageConverter<?>> list = converters.orderedStream().collect(Collectors.toList());
        list.removeIf(o -> o instanceof MappingJackson2HttpMessageConverter);
        list.add(new MappingJackson2HttpMessageConverter(JacksonObjectMapperInstance.getMapper()));
        return new HttpMessageConverters(list);
    }

}
