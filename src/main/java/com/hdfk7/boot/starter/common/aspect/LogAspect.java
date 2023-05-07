package com.hdfk7.boot.starter.common.aspect;

import com.hdfk7.boot.starter.common.constants.RequestParamConst;
import com.hdfk7.boot.starter.common.util.IpUtil;
import com.hdfk7.proto.base.util.JsonUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class LogAspect {
    public static final String ERROR_PATH_METHOD_NAME = "getErrorPath";

    private static final String HEAD = "HEAD";

    /**
     * 初始化日志切面信息
     *
     * @param joinPoint
     */
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
        // 过滤参数校验信息
        for (Object object : pointArgs) {
            if (!(object instanceof BindingResult || object instanceof ServletRequest)) {
                argList.add(object);
            }
        }
        requestContext.setAttribute(RequestParamConst.PARAMETERS, JsonUtil.toJsonStr(argList));
        String methodName = (String) requestContext.getAttribute(RequestParamConst.METHOD_NAME);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(attributes) && !ignoreRequest(methodName, attributes.getRequest().getMethod())) {
            String url = attributes.getRequest().getRequestURL().toString();
            String httpMethod = attributes.getRequest().getMethod();
            //增加请求处理前日志， 用于定位请求错误时拿到相应的参数
            log.info(String.format("requestBegin: url[%s],httpMethod[%s],request[%s]", url, httpMethod, JsonUtil.toJsonStr(argList)));
        }
    }

    private boolean ignoreRequest(String methodName, String method) {
        return ERROR_PATH_METHOD_NAME.equalsIgnoreCase(methodName) || HEAD.equalsIgnoreCase(method);
    }

    /**
     * 执行任务
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    public Object doTask(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

    /**
     * 完成任务
     *
     * @param ret
     * @return
     */
    public void finishTask(Object ret) {
        String response = "";
        if (Objects.nonNull(ret)) {
            response = JsonUtil.toJsonStr(ret);
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
        // 不打印获取error路径的请求日志
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
