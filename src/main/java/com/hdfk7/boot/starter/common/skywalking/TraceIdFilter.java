package com.hdfk7.boot.starter.common.skywalking;

import com.hdfk7.boot.starter.common.constants.TraceIdConst;
import feign.RequestInterceptor;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@ConditionalOnClass(value = {RequestInterceptor.class, Filter.class, TraceContext.class})
@ConditionalOnProperty(prefix = "skywalking", name = "trace.enable", havingValue = "true")
public class TraceIdFilter extends OncePerRequestFilter {
    @Trace
    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain chain) throws ServletException, IOException {
        String tid = TraceContext.traceId();
        response.setHeader(TraceIdConst.TID, tid);
        chain.doFilter(request, response);
    }
}