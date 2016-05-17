/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security;

import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.Datagrid;
import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.IpUtils;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.core.aop.SecurityLogAspect;
import com.eryansky.modules.sys.entity.Role;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.ResourceManager;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.utils.AppConstants;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * 系统使用的特殊工具类 简化代码编写.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2012-10-18 上午8:25:36
 */
public class SecurityUtils {

    private static final Logger logger = LoggerFactory
            .getLogger(SecurityUtils.class);
    private static ResourceManager resourceManager = SpringContextHolder.getBean(ResourceManager.class);
    private static UserManager userManager = SpringContextHolder.getBean(UserManager.class);
    private static SecurityLogAspect securityLogAspect = SpringContextHolder.getBean(SecurityConstants.SERVICE_SECURITY_LOGINASPECT);
    private static ApplicationSessionContext applicationSessionContext = ApplicationSessionContext.getInstance();

    /**
     * 是否授权某个资源
     *
     * @param resourceCode 资源编码
     * @return
     */
    public static Boolean isPermitted(String resourceCode) {
        Boolean flag = false;
        try {
            User superUser = userManager.getSuperUser();
            SessionInfo sessionInfo = getCurrentSessionInfo();
            if (sessionInfo != null && superUser != null
                    && sessionInfo.getUserId().equals(superUser.getId())) {// 超级用户
                flag = true;
            } else {
                flag = resourceManager.isUserPermittedResourceCode(sessionInfo.getUserId(), resourceCode);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 是否授权某个角色
     *
     * @param roleCode 角色编码
     * @return
     */
    public static Boolean isPermittedRole(String roleCode) {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        return isPermittedRole(sessionInfo.getUserId(), roleCode);
    }

    /**
     * 判断某个用户是否授权某个角色
     *
     * @param userId   用户ID
     * @param roleCode 角色编码
     * @return
     */
    public static Boolean isPermittedRole(Long userId, String roleCode) {
        boolean flag = false;
        try {
            if (userId == null) {
                SessionInfo sessionInfo = getCurrentSessionInfo();
                if (sessionInfo != null) {
                    userId = sessionInfo.getUserId();
                }
            }
            if (userId == null) {
                throw new SystemException("用户[" + userId + "]不存在.");
            }

            UserManager userManager = SpringContextHolder.getBean(UserManager.class);
            User superUser = userManager.getSuperUser();
            if (userId != null && superUser != null
                    && userId.equals(superUser.getId())) {// 超级用户
                flag = true;
            } else {
                User user = userManager.loadById(userId);
                List<Role> userRoles = user.getRoles();
                for (Role role : userRoles) {
                    if (roleCode.equalsIgnoreCase(role.getCode())) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return flag;
    }

    /**
     * User转SessionInfo.
     *
     * @param user
     * @return
     */
    public static SessionInfo userToSessionInfo(User user) {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setUserId(user.getId());
        sessionInfo.setName(user.getName());
        sessionInfo.setLoginName(user.getLoginName());
        sessionInfo.setRoleIds(user.getRoleIds());
        sessionInfo.setRoleNames(user.getRoleNames());
        sessionInfo.setLoginOrganId(user.getDefaultOrganId());
        sessionInfo.setLoginOrganSysCode(user.getDefaultOrganSysCode());
        sessionInfo.setLoginOrganName(user.getDefaultOrganName());
        sessionInfo.setOrganNames(user.getOrganNames());
        sessionInfo.setName(user.getName());
        return sessionInfo;
    }

    /**
     * 将用户放入session中.
     *
     * @param user
     */
    public static synchronized void putUserToSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        if(logger.isDebugEnabled()){
            logger.debug("putUserToSession:{}", sessionId);
        }
        SessionInfo sessionInfo = userToSessionInfo(user);
        sessionInfo.setIp(IpUtils.getIpAddr(request));
        sessionInfo.setId(sessionId);
        request.getSession().setAttribute(SecurityConstants.SESSION_SESSIONINFO, sessionInfo);
        SecurityConstants.sessionInfoMap.put(sessionId, sessionInfo);
    }

    /**
     * 获取当前用户session信息.
     */
    public static SessionInfo getCurrentSessionInfo() {
        SessionInfo sessionInfo = null;
        try {
            sessionInfo = SpringMVCHolder.getSessionAttribute(SecurityConstants.SESSION_SESSIONINFO);
        } catch (Exception e) {
//            logger.error(e.getMessage(),e);
        }
        return sessionInfo;
    }

    /**
     * 获取当前登录用户信息.
     */
    public static User getCurrentUser() {
        SessionInfo sessionInfo = getCurrentSessionInfo();
        User user = null;
        if(sessionInfo != null){
            user = userManager.loadById(sessionInfo.getUserId());
        }
        return user;
    }

    /**
     * 根据用户ID获取用户对象
     * @param userId
     * @return
     */
    public static User getUserById(String userId) {
        Long uId = Long.valueOf(userId);
        User user = null;
        if(uId != null){
            user = userManager.loadById(uId);
        }
        return user;
    }

    /**
     * 将用户信息从session中移除
     *
     * @param sessionId session ID
     * @param saveLog   是否 保存切面日志
     */
    public static synchronized void removeUserFromSession(String sessionId, boolean saveLog,SecurityType securityType) {
        if (StringUtils.isNotBlank(sessionId)) {
            Set<String> keySet = SecurityConstants.sessionInfoMap.keySet();
            for (String key : keySet) {
                if (key.equals(sessionId)) {
                    if(logger.isDebugEnabled()){
                        logger.debug("removeUserFromSession:{}", sessionId);
                    }
                    if (saveLog) {
                        SessionInfo sessionInfo = SecurityConstants.sessionInfoMap.get(key);
                        securityLogAspect.saveLog(sessionInfo, null, securityType);
                    }
                    SecurityConstants.sessionInfoMap.remove(key);
                }
            }
            HttpSession session = applicationSessionContext.getSession(sessionId);
            if(session != null){
                session.removeAttribute(SecurityConstants.SESSION_SESSIONINFO);
                applicationSessionContext.removeSession(session);
            }
        }
    }

    /**
     * 查看当前登录用户信息
     * @return
     */
    public static Datagrid<SessionInfo> getSessionUser() {
        List<SessionInfo> list = Lists.newArrayList();
        Set<String> keySet = SecurityConstants.sessionInfoMap.keySet();
        for (String key : keySet) {
            SessionInfo sessionInfo = SecurityConstants.sessionInfoMap.get(key);
            list.add(sessionInfo);
        }
        //排序
        Collections.sort(list, new Comparator<SessionInfo>() {
            @Override
            public int compare(SessionInfo o1, SessionInfo o2) {
                return o2.getLoginTime().compareTo(o1.getLoginTime());
            }
        });

        Datagrid<SessionInfo> dg = new Datagrid<SessionInfo>(SecurityConstants.sessionInfoMap.size(), list);
        return dg;
    }


    /**
     * 查看某个用户登录信息
     * @param loginName 登录帐号
     * @return
     */
    public static List<SessionInfo> getSessionUser(String loginName) {
        Datagrid<SessionInfo> datagrid = getSessionUser();
        List<SessionInfo> sessionInfos = Lists.newArrayList();
        for(SessionInfo sessionInfo: datagrid.getRows()){
            if(sessionInfo.getLoginName().equals(loginName)){
                sessionInfos.add(sessionInfo);
            }
        }
        return sessionInfos;
    }

    /**
     * 根据SessionId查找对应的SessionInfo信息
     * @param sessionId
     * @return
     */
    public static SessionInfo getSessionInfo(String sessionId) {
        Datagrid<SessionInfo> datagrid = getSessionUser();
        for(SessionInfo sessionInfo: datagrid.getRows()){
            if(sessionInfo.getId().equals(sessionId)){
                return sessionInfo;
            }
        }
        return null;
    }


    /**
     * 云盘管理员 超级管理 + 系统管理员 + 网盘管理员
     * @param userId 用户ID 如果为null,则为当前登录用户ID
     * @return
     */
    public static boolean isDiskAdmin(Long userId){
        Long _userId = userId;
        if(_userId == null){
            SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
            _userId = sessionInfo.getUserId();
        }

        boolean isAdmin = false;
        if (userManager.isSuperUser(_userId) || SecurityUtils.isPermittedRole(AppConstants.ROLE_SYSTEM_MANAGER) || SecurityUtils.isPermittedRole(AppConstants.ROLE_DISK_MANAGER)) {//系统管理员 + 网盘管理员
            isAdmin = true;
        }
        return isAdmin;
    }
}

