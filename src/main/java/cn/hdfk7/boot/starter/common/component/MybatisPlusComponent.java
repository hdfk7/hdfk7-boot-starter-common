package cn.hdfk7.boot.starter.common.component;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import cn.hdfk7.boot.starter.common.properties.MybatisPlusProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnClass(value = {MybatisPlusInterceptor.class})
@RequiredArgsConstructor
public class MybatisPlusComponent {
    private final MybatisPlusProperties mybatisPlusProperties;

    @Bean
    @ConditionalOnMissingBean(value = {MybatisPlusInterceptor.class})
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(mybatisPlusProperties.getDialect()));
        return interceptor;
    }

}
