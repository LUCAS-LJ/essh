/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.aop;

import com.eryansky.common.orm.hibernate.DefaultEntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.browser.BrowserType;
import com.eryansky.common.utils.browser.BrowserUtils;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.core.aop.annotation.Logging;
import com.eryansky.core.security.SecurityConstants;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.sys._enum.LogType;
import com.eryansky.modules.sys.entity.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Date;

/**
 * 日志拦截
 */
// 使用@Aspect 定义一个切面类
@Aspect
@Component(value = SecurityConstants.SERVICE_SECURITY_LOGINASPECT)
public class LogAspect {

    private static Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private DefaultEntityManager defaultEntityManager;

    /**
     * @param point 切入点
     */

    @Around("execution(* com.eryansky.modules.*.service..*Manager.*(..))")
    public Object logAll(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        // 执行方法名
        String methodName = point.getSignature().getName();
        String className = point.getTarget().getClass().getSimpleName();
        String userName = null;
        Long start = 0L;
        Long end = 0L;
        String ip = null;
        // 当前用户
        try {
            // 执行方法所消耗的时间
            start = System.currentTimeMillis();
            result = point.proceed();
            end = System.currentTimeMillis();

            // 登录名
            SessionInfo sessionInfo = null;
            try {
                sessionInfo = SecurityUtils.getCurrentSessionInfo();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
            if (sessionInfo != null) {
                userName = StringUtils.isBlank(sessionInfo.getName()) ? sessionInfo.getLoginName() : sessionInfo.getName();
                ip = sessionInfo.getIp();
            } else {
                userName = "系统";
                ip = "127.0.0.1";
//                logger.warn("sessionInfo为空.");
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(),e);
            throw e;
        }
        String name = className;
        // 操作类型
        String opertype = methodName;

        //注解式日志
        MethodSignature joinPointObject = (MethodSignature) point.getSignature();
        Method method = joinPointObject.getMethod();
        Logging logging = method.getAnnotation(Logging.class);//注解式日志
        boolean loglog = false;
        String remark = null;
        String newLogValue = null;
        if(logging != null && logging.logging() == true){
            loglog = true;
            String logValue = logging.value();
            newLogValue = logValue;
            if(StringUtils.isNotBlank(logValue)){
                Object[] args = point.getArgs();
                newLogValue = MessageFormat.format(logValue,args);
            }

            remark = newLogValue;
        }

        if (loglog == true ||
                ((opertype.indexOf("save") > -1 || opertype.indexOf("update") > -1 ||
                        opertype.indexOf("delete") > -1 || opertype.indexOf("remove") > -1 || opertype.indexOf("merge") > -1) && (logging != null && logging.logging() != false))) {
            Long time = end - start;
            Log log = new Log();
            log.setType(LogType.operate.getValue());
            log.setLoginName(userName);
            log.setModule(name);
            log.setAction(opertype);
            log.setOperTime(new Date(start));
            log.setActionTime(time.toString());
            log.setIp(ip);
            log.setRemark(remark);
            BrowserType browserType = BrowserUtils.getBrowserType(SpringMVCHolder.getRequest());
            log.setBrowserType(browserType == null ? null : browserType.toString());
            defaultEntityManager.save(log);
        }
        if(logger.isDebugEnabled()){
            logger.debug("用户:{},操作类：{},操作方法：{},耗时：{}ms.",new Object[]{userName,className,methodName,end - start});
        }
        return result;
    }


}
