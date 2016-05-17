package com.eryansky.modules.disk.entity;

import com.eryansky.common.orm.entity.BaseEntity;
import com.eryansky.common.utils.PrettyMemoryUtils;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.eryansky.modules.disk.entity._enum.FileType;
import com.eryansky.modules.sys.utils.UserUtils;
import com.eryansky.utils.AppConstants;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 文件
 */
@Entity
@Table(name = "T_DISK_FILE")
// jackson标记不生成json对象的属性
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler", "fieldHandler",
        "folder"})
@JsonFilter(" ")
//@Delete(propertyName = "status", type = PropertyType.I)
@SuppressWarnings("serial")
public class File extends BaseEntity implements Serializable {

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件标识 用户ID + "_" + hex(md5(filename + now nano time + counter++))
     * ${@link com.eryansky.core.web.upload.FileUploadUtils.encodingFilenamePrefix}
     * 区别于文件的md5
     */
    private String code;
    /**
     * 存储路径
     */
    private String filePath;

    /**
     * 文件大小 单位 字节
     */
    private Long fileSize;
    /**
     * 文件后缀
     */
    private String fileSuffix;
    /**
     * 关键字
     */
    private String keyword;
    /**
     * 备注
     */
    private String remark;
    /**
     * 文件分类 {@link com.eryansky.modules.disk.entity._enum.FileType}
     */
    private Integer fileType = FileType.Other.getValue();
    /**
     * 所属文件夹
     */
    private Folder folder;
    /**
     * 所属用户
     */
    private Long userId;
    /**
     * 分享用户 从该用户分享文件
     */
    private Long shareUserId;


    /**
     * 构造方法
     */
    public File() {
    }

    @Column(name = "NAME", length = 512,nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Column(length = 128,nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Column(length = 4096)
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Column(length = 36)
    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }


    @Column(length = 256)
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Column(length = 256)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "FOLDER_ID")
    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getShareUserId() {
        return shareUserId;
    }

    public void setShareUserId(Long shareUserId) {
        this.shareUserId = shareUserId;
    }

    @Transient
    public String getUserName(){
        return UserUtils.getUserName(userId);
    }

    @Transient
    public String getPrettyFileSize() {
        return PrettyMemoryUtils.prettyByteSize(fileSize);
    }


    /**
     * 文件复制
     * @return
     */
    public File copy(){
        File f = new File();
        f.setName(this.getName());
        f.setFilePath(this.filePath);
        f.setCode(this.getCode());
        f.setFileSuffix(this.getFileSuffix());
        f.setFileSize(this.getFileSize());
        f.setFileType(this.getFileType());
        f.setKeyword(this.getKeyword());
        f.setRemark(this.getRemark());
        f.setShareUserId(this.getUserId());
        return f;
    }

    /**
     * 获取对应磁盘文件
     * @return
     */
    @Transient
    public java.io.File getDiskFile(){
        return new java.io.File(AppConstants.getDiskBasePath()+java.io.File.separator+this.getFilePath());
    }
}
