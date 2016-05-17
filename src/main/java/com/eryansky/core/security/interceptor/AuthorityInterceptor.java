/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security.interceptor;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.google.common.collect.Lists;
import com.eryansky.core.security.SecurityConstants;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security._enum.Logical;
import com.eryansky.core.security.annotation.RequiresPermissions;
import com.eryansky.core.security.annotation.RequiresRoles;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.modules.sys.service.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;


/**
 * 权限拦截器
 * 优先级：注解>数据库权限配置
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-01-21 12:23
 */
public class AuthorityInterceptor extends HandlerInterceptorAdapter {


    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static ResourceManager resourceManager = SpringContextHolder.getBean(ResourceManager.class);

    private List<String> excludeUrls = Lists.newArrayList();// 不需要拦截的资源

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        //登录用户
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        String requestUrl = request.getRequestURI();
        //注解处理
        Boolean annotationHandler = this.annotationHandler(request,response,o,sessionInfo,requestUrl);
        if(annotationHandler != null && annotationHandler == true){
            return annotationHandler;
        }
        //数据库处理
        return this.dbHandler(request,response,o,sessionInfo,requestUrl);
    }


    /**
     * 注解处理
     * @param request
     * @param response
     * @param o
     * @param sessionInfo
     * @param requestUrl
     * @return
     * @throws Exception
     */
    private Boolean annotationHandler(HttpServletRequest request, HttpServletResponse response, Object o,
                                      SessionInfo sessionInfo,String requestUrl) throws Exception {
        HandlerMethod handler = null;
        try {
            handler = (HandlerMethod) o;
        } catch (ClassCastException e) {
//                logger.error(e.getMessage(),e);
        }

        if (handler != null) {
            Object bean = handler.getBean();
            //需要登录
            RequiresUser methodRequiresUser = handler.getMethodAnnotation(RequiresUser.class);
            if (methodRequiresUser != null && methodRequiresUser.required() == false) {
                return true;
            }

            if(methodRequiresUser == null){//类注解处理
                RequiresUser classRequiresUser =  this.getAnnotation(bean.getClass(),RequiresUser.class);
                if (classRequiresUser != null && classRequiresUser.required() == false) {
                    return true;
                }
            }

            //角色要求注解
            RequiresRoles requiresRoles = handler.getMethodAnnotation(RequiresRoles.class);
            if (requiresRoles != null) {//方法注解处理
                String[] roles = requiresRoles.value();
                for (String role : roles) {
                    boolean permittedRole = SecurityUtils.isPermittedRole(role);
                    if (Logical.AND.equals(requiresRoles.logical())) {
                        if (permittedRole == false) {
                            notPermittedRole(request,response,sessionInfo,requestUrl,role);
                            return false;
                        }
                    } else {
                        if (permittedRole == true) {
                            return true;
                        }
                    }
                }
            }else{//类注解处理
                requiresRoles = this.getAnnotation(bean.getClass(),RequiresRoles.class);
                if(requiresRoles != null){
                    if (requiresRoles != null) {
                        String[] roles = requiresRoles.value();
                        for (String role : roles) {
                            boolean permittedRole = SecurityUtils.isPermittedRole(role);
                            if (Logical.AND.equals(requiresRoles.logical())) {
                                if (permittedRole == false) {
                                    notPermittedRole(request,response,sessionInfo,requestUrl,role);
                                    return false;
                                }
                            } else {
                                if (permittedRole == true) {
                                    return true;
                                }
                            }
                        }
                    }
                }

            }

            //资源/权限 要求注解
            RequiresPermissions requiresPermissions = handler.getMethodAnnotation(RequiresPermissions.class);
            if (requiresPermissions != null) {//方法注解处理
                String[] permissions = requiresPermissions.value();
                for (String permission : permissions) {
                    boolean permittedResource = SecurityUtils.isPermitted(permission);
                    if (Logical.AND.equals(requiresPermissions.logical())) {
                        if (permittedResource == false) {
                            notPermittedPermission(request,response,sessionInfo,requestUrl,permission);
                            return false;
                        }
                    } else {
                        if (permittedResource == true) {
                            return true;
                        }
                    }
                }
            }else{//类注解处理
                requiresPermissions = this.getAnnotation(bean.getClass(),RequiresPermissions.class);
                if(requiresPermissions != null){
                    String[] permissions = requiresPermissions.value();
                    for (String permission : permissions) {
                        boolean permittedResource = SecurityUtils.isPermitted(permission);
                        if (Logical.AND.equals(requiresPermissions.logical())) {
                            if (permittedResource == false) {
                                notPermittedPermission(request,response,sessionInfo,requestUrl,permission);
                                return false;
                            }
                        } else {
                            if (permittedResource == true) {
                                return true;
                            }
                        }
                    }
                }
            }

        }

        return null;
    }

    /**
     * 未授权资源权限
     * @param request
     * @param response
     * @param sessionInfo
     * @param requestUrl
     * @param permission
     * @throws ServletException
     * @throws IOException
     */
    private void notPermittedPermission(HttpServletRequest request,HttpServletResponse response,
                                        SessionInfo sessionInfo,String requestUrl,String permission) throws ServletException, IOException {
        logger.warn("用户[{}]无权访问URL:{}，未被授权资源:{}", new Object[]{sessionInfo.getLoginName(), requestUrl,permission});
        request.getRequestDispatcher(SecurityConstants.SESSION_UNAUTHORITY_PAGE).forward(request, response);
    }


    /**
     * 未授权资源权限
     * @param request
     * @param response
     * @param sessionInfo
     * @param requestUrl
     * @throws ServletException
     * @throws IOException
     */
    private void notPermitted(HttpServletRequest request,HttpServletResponse response,
                              SessionInfo sessionInfo,String requestUrl) throws ServletException, IOException {
        logger.warn("用户{}未被授权URL:{}！", new Object[]{sessionInfo.getLoginName(), requestUrl});
        request.getRequestDispatcher(SecurityConstants.SESSION_UNAUTHORITY_PAGE).forward(request, response);
    }

    /**
     * 数据库权限处理
     * @param request
     * @param response
     * @param handler
     * @param sessionInfo
     * @param requestUrl
     * @return
     * @throws Exception
     */
    private Boolean dbHandler(HttpServletRequest request, HttpServletResponse response, Object handler,
                              SessionInfo sessionInfo,String requestUrl) throws Exception {
        // 不拦截的URL
        if (Collections3.isNotEmpty(excludeUrls)) {
            for(String excludeUrl:excludeUrls){
                boolean flag = StringUtils.simpleWildcardMatch(excludeUrl,requestUrl);
                if(flag){
                    return true;
                }
            }
        }

        if(sessionInfo != null){
            //清空session中清空未被授权的访问地址
            Object unAuthorityUrl = request.getSession().getAttribute(SecurityConstants.SESSION_UNAUTHORITY_URL);
            if(unAuthorityUrl != null){
                request.getSession().setAttribute(SecurityConstants.SESSION_UNAUTHORITY_URL,null);
            }

            String url = StringUtils.replaceOnce(requestUrl, request.getContextPath(), "");
            //检查用户是否授权该URL
            boolean isAuthority = resourceManager.isAuthority(url,sessionInfo.getUserId());
            if(!isAuthority){
                notPermitted(request,response,sessionInfo,requestUrl);
                return false; //返回到登录页面
            }

            return true;
        }else{
            request.getRequestDispatcher(SecurityConstants.SESSION_UNAUTHORITY_LOGIN_PAGE).forward(request, response);
            return false; //返回到登录页面
        }
    }

    /**
     * 未授权角色
     * @param request
     * @param response
     * @param sessionInfo
     * @param requestUrl
     * @param role
     * @throws ServletException
     * @throws IOException
     */
    private void notPermittedRole(HttpServletRequest request,HttpServletResponse response,
                                  SessionInfo sessionInfo,String requestUrl,String role) throws ServletException, IOException {
        logger.warn("用户[{}]无权访问URL:{}，未被授权角色:{}", new Object[]{sessionInfo.getLoginName(), requestUrl,role});
        request.getRequestDispatcher(SecurityConstants.SESSION_UNAUTHORITY_PAGE).forward(request, response);
    }

    private <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
        T result = clazz.getAnnotation(annotationType);
        if (result == null) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return getAnnotation(superclass, annotationType);
            } else {
                return null;
            }
        } else {
            return result;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        if(e != null){

        }
    }


    public List<String> getExcludeUrls() {
        return excludeUrls;
    }

    public void setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }
}
