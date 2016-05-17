/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.notice.entity;

import com.eryansky.common.orm.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.modules.notice._enum.IsTop;
import com.eryansky.modules.notice._enum.NoticeMode;
import com.eryansky.modules.notice.entity.common.INotice;
import com.eryansky.modules.notice.utils.NoticeUtils;
import com.eryansky.modules.sys.utils.DictionaryUtils;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.YesOrNo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 内部通知 实体类
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_notice_notice")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
@JsonFilter(" ")
public class Notice extends BaseEntity implements INotice {

    public static final String DATE_TIME_SHORT_FORMAT = "yyyy-MM-dd HH:mm";

    /**
     * 通知公告标题
     */
    private String title;
    /**
     * 通知公告类型 {@DictionaryUtils.DIC_NOTICE}
     */
    private String type;
    /**
     * 通知公告内容
     */
    private String content;
    /**
     * 附件
     */
    private List<Long> fileIds = new ArrayList<Long>(0);


    /**
     * 发布人
     */
    private Long userId;
    /**
     * 发布部门
     */
    private Long organId;


    /**
     * 0表示不置顶，1表示置顶
     */
    private Integer isTop;
    /**
     * 结束置顶天数
     */
    private Integer endTopDay;

    /**
     * 状态 默认：未发布 {@link com.eryansky.modules.notice._enum.NoticeMode}
     */
    private Integer noticeMode = NoticeMode.UnPublish.getValue();
    /**
     * 发布日期
     */
    private Date publishTime;

    /**
     * 生效时间
     */
    private Date effectTime = Calendar.getInstance().getTime();
    /**
     * 失效时间 为空，则一直有效
     */
    private Date endTime;
    /**
     * 是否记录 查看情况
     */
    private Integer isRecordRead = YesOrNo.YES.getValue();

    /**
     * 是否发给所有人，否则需要指定接收人或部门 默认值：是
     */
    private Integer isToAll = YesOrNo.YES.getValue();

    /**
     * 接收人
     */
    private List<Long> noticeUserIds = new ArrayList<Long>(0);
    /**
     * 接收部门
     */
    private List<Long> noticeOrganIds = new ArrayList<Long>(0);


    public void setTitle(String title) {
        this.title = title;
    }

    @Column(length = 512)
    public String getTitle() {
        return this.title;
    }

    @Column(length = 36)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @JsonIgnore
    @Column(length = 4096)
    public String getContent() {
        return this.content;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }


    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    public Integer getIsTop() {
        return this.isTop;
    }

    public void setEndTopDay(Integer endTopDay) {
        this.endTopDay = endTopDay;
    }

    public Integer getEndTopDay() {
        return this.endTopDay;
    }



    public void setNoticeMode(Integer noticeMode) {
        this.noticeMode = noticeMode;
    }

    public Integer getNoticeMode() {
        return this.noticeMode;
    }

    @JsonFormat(pattern = DATE_TIME_FORMAT, timezone = TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getPublishTime() {
        return publishTime;
    }


    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }


    public void setEffectTime(Date effectTime) {
        this.effectTime = effectTime;
    }

    @JsonFormat(pattern = DATE_TIME_SHORT_FORMAT, timezone = TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEffectTime() {
        return this.effectTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @JsonFormat(pattern = DATE_TIME_SHORT_FORMAT, timezone = TIMEZONE)
    @Temporal(TemporalType.TIMESTAMP)
    public Date getEndTime() {
        return this.endTime;
    }

    public Integer getIsRecordRead() {
        return isRecordRead;
    }

    public void setIsRecordRead(Integer isRecordRead) {
        this.isRecordRead = isRecordRead;
    }

    public Integer getIsToAll() {
        return isToAll;
    }

    public void setIsToAll(Integer isToAll) {
        this.isToAll = isToAll;
    }


    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "T_NOTICE_NOTICE_FILE", joinColumns = {@JoinColumn(name = "NOTICE_ID")})
    @Column(name = "FILE_ID")
    public List<Long> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<Long> fileIds) {
        this.fileIds = fileIds;
    }

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "T_NOTICE_NOTICE_ORGAN", joinColumns = {@JoinColumn(name = "NOTICE_ID")})
    @Column(name = "ORGAN_ID")
    public List<Long> getNoticeOrganIds() {
        return noticeOrganIds;
    }

    public void setNoticeOrganIds(List<Long> noticeOrganIds) {
        this.noticeOrganIds = noticeOrganIds;
    }


    @ElementCollection
    @CollectionTable(name = "T_NOTICE_NOTICE_USER", joinColumns = {@JoinColumn(name = "NOTICE_ID")})
    @Column(name = "USER_ID")
    public List<Long> getNoticeUserIds() {
        return noticeUserIds;
    }

    public void setNoticeUserIds(List<Long> noticeUserIds) {
        this.noticeUserIds = noticeUserIds;
    }


    /**
     * 通知类型 由数据字典转换而来
     *
     * @return
     */
    @Transient
    public String getTypeView() {
        return DictionaryUtils.getDictionaryNameByDV(NoticeUtils.DIC_NOTICE, this.getType(), this.getType());
    }

    @Transient
    @Override
    public String getIsTopView() {
        IsTop s = IsTop.getIsTop(noticeMode);
        String str = "";
        if(s != null){
            str =  s.getDescription();
        }
        return str;
    }

    @Transient
    @Override
    public String getNoticeModeView() {
        NoticeMode s = NoticeMode.getNoticeMode(noticeMode);
        String str = "";
        if(s != null){
            str =  s.getDescription();
        }
        return str;
    }

    /**
     * 是否发布 显示 {@link com.jfit.modules.notice._enum.NoticeMode}
     *
     * @return
     */
    @Transient
    public String getIsPublishView() {
        if (noticeMode != null) {
            return NoticeMode.getNoticeMode(noticeMode).getDescription();
        }
        return null;
    }

    /**
     * 发布人姓名
     * @return
     */
    @Transient
    public String getPublishUserName(){
        return UserUtils.getUserName(this.userId);
    }

    /**
     * 发布部门 名称
     * @return
     */
    @Transient
    public String getPublishOrganName(){
        return OrganUtils.getOrganName(this.organId);
    }

    /**
     * 通知接收人员
     *
     * @return
     */
    @Transient
    public String getNoticeUserNames() {
        return UserUtils.getUserNames(this.noticeUserIds);
    }

    /**
     * 通知接收部门
     *
     * @return
     */
    @Transient
    public String getNoticeOrganNames() {
        return OrganUtils.getOrganNames(this.noticeOrganIds);
    }

    /**
     * 通知ID
     * @return
     */
    @Transient
    public Long getNoticeId() {
        return this.id;
    }

    /**
     * 转发
     * @return
     */
    @Transient
    public Notice repeat() {
        Notice notice = new Notice();
        notice.setTitle(NoticeUtils.MSG_REPEAT + this.getTitle());
        notice.setContent(this.getContent());
        notice.setType(this.getType());
        notice.setIsTop(this.getIsTop());
        notice.setEndTopDay(this.getEndTopDay());
        notice.setEffectTime(this.getEffectTime());
        notice.setEndTopDay(this.getEndTopDay());
        return notice;
    }

}

