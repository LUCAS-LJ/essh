/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.disk.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.google.common.collect.Lists;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.disk.entity.File;
import com.eryansky.modules.disk.entity.Folder;
import com.eryansky.modules.disk.entity._enum.FolderAuthorize;
import com.eryansky.modules.disk.web.DiskController;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.RoleManager;
import com.eryansky.modules.sys.service.UserManager;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 文件夹管理
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2014-11-22
 */
@Service
public class FolderManager extends EntityManager<Folder, Long> {

    @Autowired
    private UserManager userManager;
    @Autowired
    private RoleManager roleManager;
    @Autowired
    private FileManager fileManager;

    private HibernateDao<Folder, Long> folderDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        folderDao = new HibernateDao<Folder, Long>(sessionFactory, Folder.class);
    }

    @Override
    protected HibernateDao<Folder, Long> getEntityDao() {
        return folderDao;
    }


    /**
     * 删除文件夹 包含子级文件夹以及文件
     * @param folderId
     * @param folderId
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     * @throws com.eryansky.common.exception.ServiceException
     */
    public void deleteFolderAndFiles(Long folderId) throws DaoException, SystemException, ServiceException {
        Validate.notNull(folderId, "参数[folderId]不能为null.");
        //TODO
        List<Long> fileIds = Lists.newArrayList();
        List<Long> folderIds = Lists.newArrayList();
        recursiveFolderAndFile(folderIds, fileIds, folderId);

        System.out.println(JsonMapper.getInstance().toJson(folderIds));
        System.out.println(JsonMapper.getInstance().toJson(fileIds));
        fileManager.deleteFolderFiles(SpringMVCHolder.getRequest(), fileIds);
        this.deleteByIds(folderIds);
//        for (int i = folderIds.size() - 1; i >= 0; i--) {
//            Long fId = folderIds.get(i);
//            this.deleteById(fId);
//        }

    }

    /**
     * 递归 查找文件夹下的文件夹以及文件
     * @param folderIds
     * @param fileIds
     * @param folderId
     */
    private void recursiveFolderAndFile(List<Long> folderIds, List<Long> fileIds, Long folderId){
        folderIds.add(folderId);
        List<File> folderFiles = fileManager.getFolderFiles(folderId);
        for(File folderFile:folderFiles){
            fileIds.add(folderFile.getId());
        }
        List<Folder> childFolders = this.getChildFoldersByByParentFolderId(folderId);
        if (Collections3.isNotEmpty(childFolders)){
            for(Folder childFolder:childFolders){
                recursiveFolderAndFile(folderIds, fileIds, childFolder.getId());
            }
        }
    }




    /**
     * 获取用户文创建的文件夹
     * @param userId 用户ID
     * @return
     */
    public List<Folder> getFoldersByUserId(Long userId){
        Validate.notNull(userId, "参数[userId]不能为null.");
        Parameter parameter = new Parameter(StatusState.delete.getValue(),userId);
        return getEntityDao().find("from Folder f where f.status <> :p1 and f.userId = :p2",parameter);
    }

    /**
     * 某个用户是否可以操作文件夹
     * @param userId 用户ID
     * @param folder 文件夹
     * @return
     */
    public boolean isOperateFolder(Long userId,Folder folder){
        Long _userId = userId;
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if(_userId == null){
            _userId = sessionInfo.getUserId();
        }

        boolean operateAble =  SecurityUtils.isDiskAdmin(_userId);
        if(!operateAble){
            if(sessionInfo.getUserId().equals(folder.getUserId())){
                operateAble = true;
            }
        }
        return operateAble;
    }

    /**
     * 获取用户授权使用的文件夹
     * 注:包含用户自己创建、公开、所属部门、所属角色
     * @param userId 用户ID
     * @return
     */
    public List<Folder> getAuthorizeFoldersByUserId(Long userId){
        Validate.notNull(userId, "参数[userId]不能为null.");
        Parameter parameter = new Parameter(StatusState.delete.getValue(),userId, FolderAuthorize.User.getValue(), FolderAuthorize.Pulic.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("from Folder f where f.status <> :p1")
           .append(" and ((f.userId = :p2 and f.folderAuthorize = :p3) or f.folderAuthorize = :p4");
        User user = userManager.loadById(userId);
        if(user != null){
            List<Long> userRoleIds = user.getRoleIds();
            if(Collections3.isNotEmpty(userRoleIds)){
                hql.append(" or f.roleId in (:userRoleIds)");
                parameter.put("userRoleIds",userRoleIds);
            }
            List<Long> userOrganIds = user.getOrganIds();
            if(Collections3.isNotEmpty(userRoleIds)){
                hql.append(" or f.organId in (:userOrganIds)");
                parameter.put("userOrganIds",userOrganIds);
            }

        }
        hql.append(")");
        hql.append(" order by f.folderAuthorize asc,f.createTime desc");
        logger.debug(hql.toString());
        return getEntityDao().find(hql.toString(),parameter);
    }

    /**
     * 获取部门下的文件夹
     * @param organId 机构ID
     * @param userId 用户ID
     * @param excludeUserOrganFolder 是否排除用户在部门的文件夹
     * @param parentFolderId
     * @return
     */
    public List<Folder> getOrganFolders(Long organId,Long userId,boolean excludeUserOrganFolder,Long parentFolderId){
        Validate.notNull(organId, "参数[organId]不能为null.");
        Parameter parameter = new Parameter(StatusState.normal.getValue(), FolderAuthorize.Organ.getValue(),organId);
        StringBuffer hql = new StringBuffer();
        hql.append("from Folder f where f.status = :p1 and f.folderAuthorize = :p2 and f.organId = :p3");
        if(userId != null){
            hql.append(" and f.userId ");
            if(excludeUserOrganFolder){
                hql.append(" <> ");
            }else{
                hql.append(" = ");
            }
            hql.append(" :userId ");
            parameter.put("userId",userId);
        }
        if(parentFolderId != null){
            hql.append(" and f.parentId = :parentFolderId");
            parameter.put("parentFolderId",parentFolderId);
        }else{
            hql.append(" and f.parentId is null");
        }
        hql.append(" order by f.createTime desc");
        logger.debug(hql.toString());
        return getEntityDao().find(hql.toString(),parameter);
    }

    /**
     *
     * @param folderAuthorize {@link com.eryansky.modules.disk.entity._enum.FolderAuthorize}
     * @param userId 用户ID
     * @param organId 机构ID
     * @param excludeFolderId 排除的文件夹ID
     * @param isCascade 是否级联
     * @return
     */
    public List<TreeNode> getFolders(Integer folderAuthorize,Long userId,Long organId,Long excludeFolderId,boolean isCascade){
        Validate.notNull(folderAuthorize, "参数[folderAuthorize]不能为null.");
        List<Folder> folders = this.getFoldersByFolderAuthorize(folderAuthorize,userId,organId,null,null);
        List<TreeNode> treeNodes = Lists.newArrayList();
        for(Folder folder:folders){
            if(!folder.getId().equals(excludeFolderId)){
                this.recursiveFolderTreeNode(treeNodes,folder,excludeFolderId,isCascade);
            }
        }
        return treeNodes;
    }

    /**
     * 递归文件夹树
     * @param treeNodes
     * @param folder
     */
    public void recursiveFolderTreeNode(List<TreeNode> treeNodes,Folder folder,Long excludeFolderId,boolean isCascade){
        TreeNode treeNode = new TreeNode(folder.getId().toString(),folder.getName());
        treeNode.getAttributes().put(DiskController.NODE_TYPE, DiskController.NType.Folder.toString());
        treeNode.getAttributes().put(DiskController.NODE_USERNAME, folder.getUserName());
        treeNode.setIconCls("eu-icon-folder");
        treeNodes.add(treeNode);
        if(isCascade){
            List<Folder> childFolders = this.getChildFoldersByByParentFolderId(folder.getId());
            List<TreeNode> childTreeNodes = Lists.newArrayList();
            for(Folder childFolder:childFolders){
                if(!folder.getId().equals(excludeFolderId)){
                    this.recursiveFolderTreeNode(childTreeNodes,childFolder,excludeFolderId,isCascade);
                }
            }
            for(TreeNode childTreeNode:childTreeNodes){
                treeNode.addChild(childTreeNode);
            }
        }

    }

    /**
     * 查询某个授权类型下的文件夹
     * 0个人：个人文件夹 部门：部门下的文件夹（包含自己在部门下建立的文件夹） 角色：角色下的文件夹
     * @param folderAuthorize
     * @param userId
     * @param organId
     * @param roleId
     * @param parentFolderId 上级文件夹 null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @return
     */
    public List<Folder> getFoldersByFolderAuthorize(Integer folderAuthorize,Long userId,Long organId,Long roleId,Long parentFolderId){
        Validate.notNull(folderAuthorize, "参数[folderAuthorize]不能为null.");
        Parameter parameter = new Parameter(StatusState.delete.getValue(), folderAuthorize);
        StringBuffer hql = new StringBuffer();
        hql.append("from Folder f where f.status <> :p1 and f.folderAuthorize = :p2 ");
        if(FolderAuthorize.User.getValue().equals(folderAuthorize)){
            Validate.notNull(userId, "参数[userId]不能为null.");
            hql.append(" and f.userId = :userId");
            parameter.put("userId",userId);
        }else if(FolderAuthorize.Organ.getValue().equals(folderAuthorize)){
            Validate.notNull(organId, "参数[organId]不能为null.");
            if(userId != null){
                hql.append(" and f.userId = :userId");
                parameter.put("userId",userId);
            }
            hql.append(" and f.organId = :organId");
            parameter.put("organId",organId);
        }else if(FolderAuthorize.Role.getValue().equals(folderAuthorize)){
            Validate.notNull(roleId, "参数[roleId]不能为null.");
            hql.append(" and f.roleId = :roleId");
            parameter.put("roleId",roleId);
        }else if(FolderAuthorize.Pulic.getValue().equals(folderAuthorize)){
            if(userId != null){
                hql.append(" and f.userId = :userId");
                parameter.put("userId",userId);
            }
        }else{
           throw new ServiceException("无法识别参数[folderAuthorize]："+folderAuthorize);
        }

        if(parentFolderId != null){
            hql.append(" and f.parentId = :parentFolderId");
            parameter.put("parentFolderId",parentFolderId);
        }else{
            hql.append(" and f.parentId is null");
        }
        hql.append(" order by f.createTime desc");
        logger.debug(hql.toString());
        return getEntityDao().find(hql.toString(),parameter);
    }

    /**
     * 根据父级ID查找子级文件夹
     * @param parentFolderId 父级文件夹ID null:查询顶级文件夹 不为null:查询该级下一级文件夹
     * @return
     */
    public List<Folder> getChildFoldersByByParentFolderId(Long parentFolderId){
        Parameter parameter = new Parameter(StatusState.normal.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("from Folder f where f.status = :p1 ");
        if(parentFolderId != null){
            hql.append(" and f.parentId = :parentFolderId");
            parameter.put("parentFolderId",parentFolderId);
        }else{
            hql.append(" and f.parentId is null");
        }
        hql.append(" order by f.createTime desc");
        logger.debug(hql.toString());
        return getEntityDao().find(hql.toString(),parameter);
    }

}
