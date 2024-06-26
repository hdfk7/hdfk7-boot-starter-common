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
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra == null) {
            return;
        }

        HttpServletRequest request = sra.getRequest();
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
        request.setAttribute(RequestParamConst.REQUEST_START_TIME, System.currentTimeMillis());
        request.setAttribute(RequestParamConst.METHOD_NAME, joinPoint.getSignature().getName());
        request.setAttribute(RequestParamConst.PARAMETERS, JSONUtil.toJsonStr(argList));

        String methodName = (String) request.getAttribute(RequestParamConst.METHOD_NAME);
        if (!ignoreRequest(methodName, sra.getRequest().getMethod())) {
            String url = sra.getRequest().getRequestURL().toString();
            String httpMethod = sra.getRequest().getMethod();
            log.info(String.format("requestBegin: url[%s],httpMethod[%s],request[%s]", url, httpMethod, JSONUtil.toJsonStr(argList)));
        }
    }

    public Object doTask(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }

    public void finishTask(Object ret) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra == null) {
            return;
        }

        String response = "";
        if (Objects.nonNull(ret)) {
            response = JSONUtil.toJsonStr(ret);
        }
        HttpServletRequest request = sra.getRequest();
        String methodName = (String) request.getAttribute(RequestParamConst.METHOD_NAME);
        String parameters = (String) request.getAttribute(RequestParamConst.PARAMETERS);
        Object startTime = request.getAttribute(RequestParamConst.REQUEST_START_TIME);
        request.removeAttribute(RequestParamConst.METHOD_NAME);
        request.removeAttribute(RequestParamConst.PARAMETERS);
        request.removeAttribute(RequestParamConst.REQUEST_START_TIME);
        if (ERROR_PATH_METHOD_NAME.equalsIgnoreCase(methodName) || HEAD.equalsIgnoreCase(methodName)) {
            return;
        }

        long requestStartTime = Objects.nonNull(startTime) ? (Long) startTime : System.currentTimeMillis();
        long requestFinishTime = System.currentTimeMillis();
        long cost = requestFinishTime - requestStartTime;

        String url = request.getRequestURL().toString();
        String httpMethod = request.getMethod();
        String remoteHost = IpUtil.getIpAddress(request);
        int remotePort = request.getRemotePort();

        log.info(String.format("cost[%d],url[%s],httpMethod[%s],remoteHost[%s],remotePort[%d]," +
                "request[%s],response[%s]", cost, url, httpMethod, remoteHost, remotePort, parameters, response));
    }

    protected boolean ignoreRequest(String methodName, String method) {
        return ERROR_PATH_METHOD_NAME.equalsIgnoreCase(methodName) || HEAD.equalsIgnoreCase(method);
    }
}
