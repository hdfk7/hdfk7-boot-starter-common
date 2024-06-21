package com.hdfk7.boot.starter.common.aspect;

import cn.hutool.json.JSONUtil;
import com.hdfk7.boot.starter.common.constants.RequestParamConst;
import com.hdfk7.boot.starter.common.util.IpUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class LogAspect {
    private static final String ERROR_PATH_METHOD_NAME = "getErrorPath";
    private static final String HEAD = "HEAD";

    public void init(JoinPoint joinPoint) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest requestContext = requestAttributes.getRequest();
        requestContext.setAttribute(RequestParamConst.REQUEST_START_TIME, System.currentTimeMillis());
        requestContext.setAttribute(RequestParamConst.METHOD_NAME, joinPoint.getSignature().getName());
        Object[] pointArgs = joinPoint.getArgs();
        List<Object> argList = new ArrayList<>();
        for (Object object : pointArgs) {
            if (!(object instanceof BindingResult
                    || object instanceof ServletRequest
                    || object instanceof ServletResponse
                    || object instanceof MultipartFile)) {
                argList.add(object);
            }
        }
        requestContext.setAttribute(RequestParamConst.PARAMETERS, JSONUtil.toJsonStr(argList));
        String methodName = (String) requestContext.getAttribute(RequestParamConst.METHOD_NAME);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes) && !ignoreRequest(methodName, attributes.getRequest().getMethod())) {
            String url = attributes.getRequest().getRequestURL().toString();
            String httpMethod = attributes.getRequest().getMethod();
            log.info(String.format("requestBegin: url[%s],httpMethod[%s],request[%s]", url, httpMethod, JSONUtil.toJsonStr(argList)));
        }
    }

    private boolean ignoreRequest(String methodName, String method) {
        return ERROR_PATH_METHOD_NAME.equalsIgnoreCase(methodName) || HEAD.equalsIgnoreCase(method);
    }

    public Object doTask(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

    public void finishTask(Object ret) {
        String response = "";
        if (Objects.nonNull(ret)) {
            response = JSONUtil.toJsonStr(ret);
        }

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest requestContext = requestAttributes.getRequest();
        String methodName = (String) requestContext.getAttribute(RequestParamConst.METHOD_NAME);
        String parameters = (String) requestContext.getAttribute(RequestParamConst.PARAMETERS);
        Object startTime = requestContext.getAttribute(RequestParamConst.REQUEST_START_TIME);
        requestContext.removeAttribute(RequestParamConst.METHOD_NAME);
        requestContext.removeAttribute(RequestParamConst.PARAMETERS);
        requestContext.removeAttribute(RequestParamConst.REQUEST_START_TIME);
        if (ERROR_PATH_METHOD_NAME.equalsIgnoreCase(methodName) || HEAD.equalsIgnoreCase(methodName)) {
            return;
        }

        long requestStartTime = Objects.nonNull(startTime) ? (Long) startTime : System.currentTimeMillis();
        long requestFinishTime = System.currentTimeMillis();
        long cost = requestFinishTime - requestStartTime;

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(attributes)) {
            log.info(String.format("cost[%d],request[%s],response[%s]\r\n", cost, parameters, response));
            return;
        }
        HttpServletRequest servletRequest = attributes.getRequest();

        String url = servletRequest.getRequestURL().toString();
        String httpMethod = servletRequest.getMethod();
        String remoteHost = IpUtil.getIpAddress(servletRequest);
        int remotePort = servletRequest.getRemotePort();

        log.info(String.format("cost[%d],url[%s],httpMethod[%s],remoteHost[%s],remotePort[%d]," +
                "request[%s],response[%s]\r\n", cost, url, httpMethod, remoteHost, remotePort, parameters, response));
    }
}
