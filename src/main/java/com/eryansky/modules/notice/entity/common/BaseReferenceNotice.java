/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.notice.entity.common;

import com.eryansky.common.orm.entity.AutoEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.eryansky.modules.notice._enum.NoticeReadMode;
import com.eryansky.modules.notice.entity.Notice;
import com.eryansky.modules.notice.utils.NoticeUtils;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2015-01-30
 */
@MappedSuperclass
public class BaseReferenceNotice extends AutoEntity implements INotice {

    private Long noticeId;

    private Notice notice;

    /**
     * 带缓存查询
     * @return
     */
    @JsonIgnore
    @Transient
    public Notice getNotice() {
        if(this.notice == null){
            this.notice = NoticeUtils.getNotice(noticeId);
        }
        return notice;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    @Transient
    @Override
    public String getTitle() {
        return getNotice().getTitle();
    }

    @Transient
    @Override
    public String getType() {
        return getNotice().getType();
    }

    @Transient
    @Override
    public String getTypeView() {
        return getNotice().getTypeView();
    }

    @Transient
    @Override
    public String getContent() {
        return getNotice().getContent();
    }

    @Transient
    @Override
    public String getPublishUserName() {
        return getNotice().getPublishUserName();
    }

    @Transient
    @Override
    public String getPublishOrganName() {
        return getNotice().getPublishOrganName();
    }

    @Transient
    @Override
    public Date getPublishTime() {
        return getNotice().getPublishTime();
    }

    @Transient
    @Override
    public Integer getIsTop() {
        return getNotice().getIsTop();
    }

    @Transient
    @Override
    public String getIsTopView() {
        return getNotice().getIsTopView();
    }

    @Transient
    @Override
    public String getNoticeModeView() {
        return getNotice().getNoticeModeView();
    }



    /**
     * 判断当前登录用户是否读取
     * @return
     */
    @Transient
    public boolean isRead(){
        return NoticeUtils.isRead(this.noticeId);
    }

    /**
     * 判断当前登录用户是否读取
     * @return
     */
    @Transient
    public String isReadView(){
        return this.isRead() == true ? NoticeReadMode.readed.getDescription():NoticeReadMode.unreaded.getDescription();
    }
}
