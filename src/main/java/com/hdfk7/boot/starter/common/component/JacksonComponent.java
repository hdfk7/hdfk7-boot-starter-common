package com.hdfk7.boot.starter.common.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdfk7.proto.base.json.JacksonObjectMapperInstance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(ObjectMapper.class)
public class JacksonComponent {

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonObjectMapperInstance.getMapper();
    }

}
