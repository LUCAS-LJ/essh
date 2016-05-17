/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.notice.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.modules.notice._enum.NoticeReadMode;
import com.eryansky.modules.notice.entity.common.BaseReferenceNotice;
import com.eryansky.modules.sys.utils.OrganUtils;
import com.eryansky.modules.sys.utils.UserUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * entity
 * 
 * @author xush
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "t_notice_notice_scope")
@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "fieldHandler"})
public class NoticeScope extends BaseReferenceNotice {

    /**
	 * 用户
	 */
	private Long userId;
	/**
	 * 部门
	 */
	private Long organId;
	/**
	 * 是否已读 默认值：否 {@link NoticeReadMode}
	 */
	private Integer isRead = NoticeReadMode.unreaded.getValue();
    /**
     * 读取时间
     */
    private Date readTime;

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

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }


    /**
     * 接收人姓名
     * @return
     */
    @Transient
    public String getUserName(){
        return UserUtils.getUserName(this.userId);
    }

    /**
     * 接收部门名称
     * @return
     */
    @Transient
    public String getOrganName(){
        return OrganUtils.getOrganName(this.organId);
    }

    /**
     * 是否读取
     * @return
     */
    @Transient
    public String getIsReadView(){
        NoticeReadMode s = NoticeReadMode.getNoticeReadMode(isRead);
        String str = "";
        if(s != null){
            str =  s.getDescription();
        }
        return str;
    }
}
