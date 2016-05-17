/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.disk.service;

import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.entity.Folder;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-12-10
 */
@Service
public class DiskManager {

    @Autowired
    private FolderManager folderManager;
    @Autowired
    private FileManager fileManager;


    /**
     * 根据编码获取 获取系统文件夹
     * <br/>如果不存在则自动创建
     * @param code 系统文件夹编码
     * @return
     */
    public Folder checkSystemFolderByCode(String code){
        Validate.notBlank(code, "参数[code]不能为null.");
        Parameter parameter = new Parameter(StatusState.normal.getValue(), FolderAuthorize.SysTem.getValue(),code);
        StringBuffer hql = new StringBuffer();
        hql.append("from Folder f where f.status = :p1 and f.folderAuthorize = :p2 and f.code = :p3");
        List<Folder> list =  folderManager.getEntityDao().find(hql.toString(), parameter);
        Folder folder =  list.isEmpty() ? null:list.get(0);
        if(folder == null){
            folder = new Folder();
            folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
            folder.setCode(code);
            folderManager.saveOrUpdate(folder);
        }
        return folder;
    }


    /**
     * 根据编码获取 获取用户的系统文件夹
     * <br/>如果不存在则自动创建
     * @param code 系统文件夹编码
     * @param userId 用户ID
     * @return
     */
    public Folder checkSystemFolderByCode(String code,Long userId){
        Validate.notBlank(code, "参数[code]不能为null.");
        Validate.notNull(userId, "参数[userId]不能为null.");
        Parameter parameter = new Parameter(StatusState.normal.getValue(), FolderAuthorize.SysTem.getValue(),code,userId);
        StringBuffer hql = new StringBuffer();
        hql.append("from Folder f where f.status = :p1 and f.folderAuthorize = :p2 and f.code = :p3 and f.userId = :p4");
        List<Folder> list =  folderManager.getEntityDao().find(hql.toString(), parameter);
        Folder folder =  list.isEmpty() ? null:list.get(0);
        if(folder == null){
            folder = new Folder();
            folder.setFolderAuthorize(FolderAuthorize.SysTem.getValue());
            folder.setCode(code);
            folder.setUserId(userId);
            folderManager.saveOrUpdate(folder);
        }
        return folder;
    }

    /**
     * 保存系统文件
     * @param folderCode
     * @param file
     * @return
     */
    public File saveSystemFile(String folderCode,File file){
        Validate.notBlank(folderCode, "参数[folderCode]不能为null.");
        Validate.notNull(file, "参数[file]不能为null.");
        Folder folder = checkSystemFolderByCode(folderCode);
        file.setFolder(folder);
        fileManager.save(file);
        return file;
    }

    /**
     * 保存文件
     * @param fileId 文件ID
     * @return
     */
    public File getFileById(Long fileId){
        Validate.notNull(fileId, "参数[fileId]不能为null.");
        return fileManager.loadById(fileId);
    }


    /**
     * 保存文件
     * @param file
     * @return
     */
    public File saveFile(File file){
        Validate.notNull(file, "参数[file]不能为null.");
        fileManager.save(file);
        return file;
    }

    /**
     * 修改文件
     * @param file
     * @return
     */
    public File updateFile(File file){
        Validate.notNull(file, "参数[file]不能为null.");
        fileManager.update(file);
        return file;
    }

    /**
     * 删除文件
     * @param request
     * @param file
     * @return
     */
    public void deleteFile(HttpServletRequest request,File file){
        Validate.notNull(file, "参数[file]不能为null.");
        fileManager.deleteFile(request,file.getId());
    }

    /**
     * 删除文件
     * @param request
     * @param fileId
     */
    public void deleteFile(HttpServletRequest request,Long fileId){
        Validate.notNull(fileId, "参数[fileId]不能为null.");
        fileManager.deleteFile(request,fileId);
    }




    /**
     * 根据ID查找
     * @param fileIds 文件ID集合
     * @return
     */
    public List<File> findFilesByIds(List<Long> fileIds){
        Validate.notEmpty(fileIds, "参数[fileIds]不能为null.");
        if(Collections3.isNotEmpty(fileIds)){
            return fileManager.findFilesByIds(fileIds);
        }else{
        	return null;
        }
    }

}
