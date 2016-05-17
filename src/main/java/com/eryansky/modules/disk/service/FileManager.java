/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.disk.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.core.web.upload.FileUploadUtils;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * 文件管理
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-11-22
 */
@Service
public class FileManager extends EntityManager<File, Long> {

    @Autowired
    private FolderManager folderManager;

    private HibernateDao<File, Long> fileDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        fileDao = new HibernateDao<File, Long>(sessionFactory, File.class);
    }

    @Override
    protected HibernateDao<File, Long> getEntityDao() {
        return fileDao;
    }

    /**
     * 根据文件标识获取文件
     * @param code 文件标识
     * @param excludeFileId 排除的文件ID  可为null
     * @return
     */
    private List<File> getFileByCode(String code,Long excludeFileId){
        StringBuffer hql = new StringBuffer();
        Parameter parameter = new Parameter(StatusState.delete.getValue(),code);
        hql.append("from File f where f.status <> :p1  and f.code = :p2");
        if(excludeFileId != null){
            hql.append(" and f.id <> :excludeFileId");
            parameter.put("excludeFileId",excludeFileId);
        }
        List<File> list = getEntityDao().find(hql.toString(),parameter);
        return list;
    }

    /**
     * 查找文件夹下所有文件
     * @param folderId 文件夹ID
     * @return
     */
    public List<File> getFolderFiles(Long folderId){
        Validate.notNull(folderId, "参数[folderId]不能为null.");
        Parameter parameter = new Parameter(StatusState.normal.getValue(),folderId);
        return getEntityDao().find("from File f where f.status = :p1 and f.folder.id = :p2",parameter);
    }

    public void deleteFile(HttpServletRequest request,Long fileId){
        File file = getEntityDao().load(fileId);
        try {
            //检查文件是否被引用
            List<File> files = this.getFileByCode(file.getCode(),fileId);
            if(Collections3.isEmpty(files)){
                FileUploadUtils.delete(request, file.getFilePath());//磁盘删除文件
                logger.debug("磁盘上删除文件：{}", new Object[]{file.getFilePath()});
            }
//            file.setStatus(StatusState.lock.getValue());
//            this.update(file);
            getEntityDao().delete(file);
        } catch (IOException e) {
            logger.error("删除文件[{}]失败,{}",new Object[]{file.getFilePath(),e.getMessage()});
        }catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
    /**
     *
     * 文件删除
     * @param request
     * @param fileIds 文件集合
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     * @throws com.eryansky.common.exception.ServiceException
     */
    public void deleteFolderFiles(HttpServletRequest request,List<Long> fileIds) throws DaoException, SystemException, ServiceException {
        if (Collections3.isNotEmpty(fileIds)) {
            for (Long fileId : fileIds) {
                deleteFile(request,fileId);
            }
        } else {
            logger.warn("参数[ids]为空.");
        }
    }

    public Page<File> findPage(Page<File> page,Long folderId,String fileName){
//        Validate.notNull(userId, "参数[userId]不能为null.");
        StringBuffer hql = new StringBuffer();
        Parameter parameter = new Parameter(StatusState.delete.getValue());
        hql.append("from File f where f.status <> :p1");

        if(folderId != null){
            hql.append(" and f.folder.id = :folderId");
            parameter.put("folderId",folderId);
        }

        if(StringUtils.isNotBlank(fileName)){
            hql.append(" and f.name like :fileName");
            parameter.put("fileName","%"+fileName+"%");
        }
        logger.debug(hql.toString());
        return getEntityDao().findPage(page,hql.toString(),parameter);
    }

    /**
     * 查找用户已用个人存储空间 单位：字节
     * @param userId 用户ID
     * @return
     */
    public long getUserUsedStorage(Long userId){
        Validate.notNull(userId, "参数[userId]不能为null.");
        StringBuffer hql = new StringBuffer();
        hql.append("select sum(f.fileSize) from File f where f.status <> :p1 and f.folder.folderAuthorize = :p2 and f.userId = :p3");
        Parameter parameter = new Parameter(StatusState.delete.getValue(), FolderAuthorize.User.getValue(),userId);
        List<Object> list = getEntityDao().find(hql.toString(),parameter);

        long count = 0L;
        if (list.size() > 0) {
            count = list.get(0) == null ? 0:(Long)list.get(0);
        }
        return count;
    }


    /**
     * 查找部门已用存储空间 单位：字节
     * @param organId 部门ID
     * @return
     */
    public long getOrganUsedStorage(Long organId){
        Validate.notNull(organId, "参数[organId]不能为null.");
        StringBuffer hql = new StringBuffer();
        hql.append("select sum(f.fileSize) from File f where f.status <> :p1 and f.folder.folderAuthorize = :p2 and f.folder.organId = :p3");
        Parameter parameter = new Parameter(StatusState.delete.getValue(), FolderAuthorize.Organ.getValue(),organId);
        List<Object> list = getEntityDao().find(hql.toString(),parameter);

        long count = 0L;
        if (list.size() > 0) {
            count = list.get(0) == null ? 0:(Long)list.get(0);
        }
        return count;
    }


    /**
     * 根据ID查找
     * @param fileIds
     * @return
     */
    public List<File> findFilesByIds(List<Long> fileIds){
        Parameter parameter = new Parameter(fileIds);
        return getEntityDao().find("from File f where f.id in (:p1)",parameter);
    }

}
