/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.core.security;

import com.eryansky.common.orm.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * session登录用户对象.
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-24 下午2:53:59
 *
 */
@SuppressWarnings("serial")
public class SessionInfo implements Serializable {

    /**
     * sessionID
     */
    private String id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 登录名
     */
    private String loginName;
    /**
     * 登录姓名
     */
    private String name;
    /**
     * 用户类型
     */
    private String userType;
    /**
     * 客户端IP
     */
    private String ip;
    /**
     * 角色ID集合
     */
    private List<Long> roleIds;
    /**
     * 角色名称组合
     */
    private String roleNames;
    /**
     * 部门ID
     */
    private Long loginOrganId;
    /**
     * 系统登录部门编码
     */
    private String loginOrganSysCode;
    /**
     * 系统登录部门名称
     */
    private String loginOrganName;
    /**
     * 用户属组织机构名称 以","分割
     */
    private String organNames;

    /**
     * 用户岗位
     */
    private List<Long> postIds;
    /**
     * 用户岗位名称
     */
    private String postNames;

    /**
     * 登录时间
     */
    private Date loginTime = new Date();

    public SessionInfo() {
    }

    /**
     * sessionID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置 sessionID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 登录名
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * 设置 登录名
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * 登录姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置 登录姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 用户类型
     */
    public String getUserType() {
        return userType;
    }

    /**
     * 设置 用户类型
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * 客户端IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置 客户端IP
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * 角色名称组合
     */
    public String getRoleNames() {
        return roleNames;
    }

    /**
     * 设置 角色名称组合
     */
    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    /**
     * 角色ID集合
     */
    public List<Long> getRoleIds() {
        return roleIds;
    }

    /**
     * 设置 角色ID集合
     */
    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    /**
     * 登录时间
     */
    // 设定JSON序列化时的日期格式
    @JsonFormat(pattern = BaseEntity.DATE_TIME_FORMAT, timezone = BaseEntity.TIMEZONE)
    public Date getLoginTime() {
        return loginTime;
    }

    /**
     * 设置登录时间
     */
    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Long getLoginOrganId() {
        return loginOrganId;
    }

    public void setLoginOrganId(Long loginOrganId) {
        this.loginOrganId = loginOrganId;
    }


    public String getLoginOrganSysCode() {
        return loginOrganSysCode;
    }

    public void setLoginOrganSysCode(String loginOrganSysCode) {
        this.loginOrganSysCode = loginOrganSysCode;
    }

    /**
     * 默认登录组织机构名称
     *
     * @return
     */
    public String getLoginOrganName() {
        return loginOrganName;
    }

    /**
     * 设置默认登录组织机构名称
     */
    public void setLoginOrganName(String loginOrganName) {
        this.loginOrganName = loginOrganName;
    }

    /**
     * 组织机构名称
     *
     * @return
     */
    public String getOrganNames() {
        return organNames;
    }

    /**
     * 设置组织机构名称
     */
    public void setOrganNames(String organNames) {
        this.organNames = organNames;
    }

    public List<Long> getPostIds() {
        return postIds;
    }

    public void setPostIds(List<Long> postIds) {
        this.postIds = postIds;
    }

    public String getPostNames() {
        return postNames;
    }

    public void setPostNames(String postNames) {
        this.postNames = postNames;
    }

    /**
     * 是否是超级管理员
     * @return
     */
    public boolean isSuperUser(){
        if(this.getUserId() != null &&  this.getUserId().equals(1L)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
