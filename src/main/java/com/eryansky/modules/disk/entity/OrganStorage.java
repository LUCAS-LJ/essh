/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.disk.entity;

import com.eryansky.common.orm.entity.AutoEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.utils.AppConstants;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 部门云盘存储空间配置
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-11-24
 */
@Entity
@Table(name = "T_DISK_ORGAN_STORAGE")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler"})
public class OrganStorage extends AutoEntity {

    /**
     * 部门ID
     */
    private Long organId;
    /**
     * 最大限制大小 单位：M {@link com.eryansky.utils.AppConstants}
     */
    private Integer limitSize = AppConstants.getDiskOrganLimitSize();

    public OrganStorage(Long organId) {
        this.organId = organId;
    }

    public Long getOrganId() {
        return organId;
    }

    public void setOrganId(Long organId) {
        this.organId = organId;
    }

    public Integer getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
    }
}
