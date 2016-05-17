/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.entity;

import com.eryansky.common.orm.entity.AutoEntity;
import com.eryansky.common.orm.entity.BaseEntity;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.modules.sys._enum.AppType;

import javax.persistence.*;
import java.util.Date;

/**
 * 系统更新日志
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-01-09
 */
@Entity
@Table(name = "T_SYS_VERSION_LOG")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class VersionLog extends AutoEntity {

    /**
     * 版本号
     */
    private String versionName;
    /**
     * 版本内部编号
     */
    private String versionCode;
    /**
     * 应用类型 {@link com.eryansky.modules.sys._enum.AppType}
     */
    private Integer appType = AppType.Server.getValue();
    /**
     * 链接地址/应用下载地址
     */
    private String url;
    /**
     * 更新时间
     */
    private Date updateTime = new Date();
    /**
     * 更新发布人
     */
    private String updateUser;
    /**
     * 变更说明
     */
    private String remark;

    @Column(length = 36)
    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    @Column(length = 36)
    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public Integer getAppType() {
        return appType;
    }

    public void setAppType(Integer appType) {
        this.appType = appType;
    }

    @Column(length = 1024)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @JsonFormat(pattern = BaseEntity.DATE_TIME_FORMAT, timezone = BaseEntity.TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    @Column(length = 36)
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    @Column(length = 8192)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return JsonMapper.getInstance().toJson(this);
    }
}
