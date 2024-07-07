package cn.hdfk7.boot.starter.common.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hdfk7.boot.proto.base.json.JacksonObjectMapperInstance;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(value = {ObjectMapper.class})
public class JacksonComponent {

    @Bean
    @ConditionalOnMissingBean(value = {ObjectMapper.class})
    public ObjectMapper objectMapper() {
        return JacksonObjectMapperInstance.getMapper();
    }

}
