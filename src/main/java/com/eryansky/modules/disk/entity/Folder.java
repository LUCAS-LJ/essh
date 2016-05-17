package com.eryansky.modules.disk.entity;

import com.eryansky.common.orm.PropertyType;
import com.eryansky.common.orm.annotation.Delete;
import com.eryansky.common.orm.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.sys.utils.UserUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;


/**
 * 文件夹
 */
@Entity
@Table(name = "T_DISK_FOLDER")
// jackson标记不生成json对象的属性
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler",
        "parentFolder", "subFolders"})
@Delete(propertyName = "status", type = PropertyType.I)
@SuppressWarnings("serial")
public class Folder extends BaseEntity implements Serializable {
    /**
     * 名称
     */
    private String name;
    /**
     * 存储路径
     */
    private String path;
    /**
     * 大小限制  单位：M 无限制：0
     */
    private Integer limitSize;
    /**
     * 备注
     */
    private String remark;
    /**
     * 排序
     */
    private Integer orderNo;

    /**
     * 授权 {@link com.eryansky.modules.disk.entity._enum.FolderAuthorize}
     */
    private Integer folderAuthorize = FolderAuthorize.User.getValue();
    /**
     * 文件夹标识 授权类型为System时使用
     */
    private String code;
    /**
     * 所属用户
     */
    private Long userId;
    /**
     * 所属部门
     */
    private Long organId;
    /**
     * 授权角色
     */
    private Long roleId;

    /**
     * 父级ID
     */
    private Long parentId;

    /**
     * 构造方法
     */
    public Folder() {
    }

    @Column(name = "NAME", length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Integer getLimitSize() {
        return limitSize;
    }

    public void setLimitSize(Integer limitSize) {
        this.limitSize = limitSize;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Column(name = "FOLDER_AUTHORIZE")
    public Integer getFolderAuthorize() {
        return folderAuthorize;
    }

    public void setFolderAuthorize(Integer folderAuthorize) {
        this.folderAuthorize = folderAuthorize;
    }

    @Column(name = "CODE", length = 36)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "REMARK", length = 255)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Column
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    @Transient
    public String getUserName(){
        return UserUtils.getUserName(userId);
    }
}
