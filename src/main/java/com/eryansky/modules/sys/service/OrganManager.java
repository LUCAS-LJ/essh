/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys._enum.SexType;
import com.eryansky.modules.sys.entity.Organ;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.utils.CacheConstants;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 机构Organ管理 Service层实现类.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-09-09 下午21:26:46
 */
@Service
public class OrganManager extends EntityManager<Organ, Long> {

    @Autowired
    private UserManager userManager;

    private HibernateDao<Organ, Long> organDao;// 默认的泛型DAO成员变量.

    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        organDao = new HibernateDao<Organ, Long>(sessionFactory, Organ.class);
    }

    @Override
    protected HibernateDao<Organ, Long> getEntityDao() {
        return organDao;
    }

    /**
     * 保存或修改.
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void saveOrUpdate(Organ entity) throws DaoException, SystemException,
            ServiceException {
        logger.debug("清空缓存:{}");
        Assert.notNull(entity, "参数[entity]为空!");
        organDao.saveOrUpdate(entity);
    }

    /**
     * 保存或修改.
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void merge(Organ entity) throws DaoException, SystemException,
            ServiceException {
        Assert.notNull(entity, "参数[entity]为空!");
        organDao.merge(entity);
    }

    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    @Override
    public void saveEntity(Organ entity) throws DaoException, SystemException, ServiceException {
        super.saveEntity(entity);
    }

    /**
     * 删除(根据主键ID).
     *
     * @param id
     *            主键ID
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void deleteById(final Long id){
        getEntityDao().delete(id);
    }

    /**
     * 自定义删除方法.
     */
    @CacheEvict(value = { CacheConstants.ORGAN_USER_TREE_CACHE},allEntries = true)
    public void deleteByIds(List<Long> ids) throws DaoException, SystemException,
            ServiceException {
        super.deleteByIds(ids);
    }

    /**
     * Organ转TreeNode
     * @param organ 机构
     * @param organType 机构类型
     * @param isCascade       是否级联
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    private TreeNode organToTreeNode(Organ organ,Integer organType,boolean isCascade) throws DaoException, SystemException,
            ServiceException {
        if(organType!=null){
            if(!organType.equals(organ.getType())){
                return null;
            }
        }
        TreeNode treeNode = new TreeNode(organ.getId().toString(),
                organ.getName());
        // 自定义属性 url
        Map<String, Object> attributes = Maps.newHashMap();
        attributes.put("code", organ.getCode());
        attributes.put("sysCode", organ.getSysCode());
        attributes.put("type", organ.getType());
        treeNode.setAttributes(attributes);
        if(isCascade){
            List<TreeNode> childrenTreeNodes = Lists.newArrayList();
            for(Organ subOrgan:organ.getSubOrgans()){
                TreeNode node = organToTreeNode(subOrgan,organType,isCascade);
                if(node !=null){
                    childrenTreeNodes.add(node);
                }
            }
            treeNode.setChildren(childrenTreeNodes);
        }

        return treeNode;
    }


    /**
     * @param entity
     * @param excludeOrganId
     * @param isCascade 是否级联
     * @param adduser   是否带用户
     * @return
     * @throws com.eryansky.common.exception.DaoException
     * @throws com.eryansky.common.exception.SystemException
     * @throws com.eryansky.common.exception.ServiceException
     */
    public TreeNode getTreeNode(Organ entity, Long excludeOrganId, boolean isCascade, boolean adduser,List<Long> checkedUserIds) throws DaoException, SystemException, ServiceException {
        TreeNode node = this.organToTreeNode(entity, null, false);
        List<Organ> subOrgans = this.getByParentId(entity.getId(), StatusState.normal.getValue());
        if (isCascade) {// 递归查询子节点
            if (Collections3.isNotEmpty(subOrgans)) {
                List<TreeNode> children = Lists.newArrayList();
                if (adduser) {
                    for (User user : entity.getDefautUsers()) {
                        TreeNode userNode = new TreeNode();
                        userNode.setId(user.getId().toString());
                        userNode.setText(user.getName());
                        if(SexType.girl.getValue().equals(user.getSex())){
                            userNode.setIconCls("eu-icon-user_red");
                        }else{
                            userNode.setIconCls("eu-icon-user");
                        }
                        if(Collections3.isNotEmpty(checkedUserIds)){
                            if(checkedUserIds.contains(user.getId())){
                                userNode.setChecked(true);
                            }
                        }

                        Map<String, Object> attributes = Maps.newHashMap();
                        attributes.put("nType", "u");
                        userNode.setAttributes(attributes);
                        children.add(userNode);
                    }
                }
                for (Organ d : subOrgans) {
                    boolean isInclude = true;// 是否包含到节点树
                    TreeNode treeNode = null;
                    // 排除自身
                    if (excludeOrganId != null) {
                        if (!d.getId().equals(excludeOrganId)) {
                            treeNode = getTreeNode(d, excludeOrganId, true, adduser,checkedUserIds);
                        } else {
                            isInclude = false;
                        }
                    } else {
                        treeNode = getTreeNode(d, excludeOrganId, true, adduser,checkedUserIds);
                    }
                    if (isInclude) {
                        Map<String, Object> attributes = Maps.newHashMap();
                        attributes.put("nType", "o");
                        attributes.put("type", d.getType());
                        attributes.put("sysCode", d.getSysCode());
                        treeNode.setAttributes(attributes);
                        treeNode.setIconCls("eu-icon-group");
                        children.add(treeNode);
                        node.setState(TreeNode.STATE_CLOASED);
                    } else {
                        node.setState(TreeNode.STATE_OPEN);
                    }
                }
                node.setChildren(children);
            } else {
                if (adduser) {
                    if (Collections3.isNotEmpty(entity.getDefautUsers())) {
                        List<TreeNode> userTreeNodes = Lists.newArrayList();
                        for (User user : entity.getDefautUsers()) {
                            TreeNode userNode = new TreeNode();
                            userNode.setId(user.getId().toString());
                            userNode.setText(user.getName());
                            if(SexType.girl.getValue().equals(user.getSex())){
                                userNode.setIconCls("eu-icon-user_red");
                            }else{
                                userNode.setIconCls("eu-icon-user");
                            }
                            if(Collections3.isNotEmpty(checkedUserIds)){
                                if(checkedUserIds.contains(user.getId())){
                                    userNode.setChecked(true);
                                }
                            }

                            Map<String, Object> attributes = Maps.newHashMap();
                            attributes.put("nType", "u");
                            userNode.setAttributes(attributes);
                            userTreeNodes.add(userNode);
                        }
                        node.setState(TreeNode.STATE_CLOASED);
                        node.setChildren(userTreeNodes);
                    }
                }
            }
        }
        return node;
    }

    /**
     * 用户最大权限机构（管理员 不适用此方法）
     * <br/>机构最短
     * @param userId
     * @return
     */
    private List<Organ> getUserPermissionOrgan(Long userId){
        Assert.notNull(userId, "参数[userId]为空!");
        User user = userManager.loadById(userId);
        if(user == null){
            throw new ServiceException("用户【"+userId+"】不存在或已被删除.");
        }
        List<Organ> userOrgans = user.getOrgans();//根据机构系统编码升序排列
        int minOrganLength = 0;
        List<Organ> minOrgans = Lists.newArrayList();
        if(Collections3.isNotEmpty(userOrgans)){
            Iterator<Organ> iterator = userOrgans.iterator();
            while(iterator.hasNext()){
                Organ organ = iterator.next();
                if(minOrganLength==0){
                    minOrganLength = organ.getSysCode().length();
                    minOrgans.add(organ);
                }else if(organ.getSysCode().length() <= minOrganLength){
                    minOrganLength = organ.getSysCode().length();
                    minOrgans.add(organ);
                }
            }
//            if(Collections3.isEmpty(minOrgans) && user.getDefaultOrgan() != null){
//                minOrgans.add(user.getDefaultOrgan());
//            }
        }
        return minOrgans;
    }

    /**
     * 获取用户机构树
     * @param userId
     * @return
     */
    @Cacheable(CacheConstants.ORGAN_USER_TREE_CACHE)
    public List<TreeNode> getUserOrganTree(Long userId){
        Assert.notNull(userId, "参数[userId]为空!");
        List<TreeNode> treeNodes = Lists.newArrayList();
        User sessionUser = userManager.loadById(userId);
        User sueperUser = userManager.getSuperUser();
        if(sessionUser !=null && sueperUser !=null && sessionUser.getId().equals(sueperUser.getId())){
            treeNodes = this.getOrganTree(null,null,true,false,null);
        }else{
            List<Organ> organs = this.getUserPermissionOrgan(userId);
            for (Organ rs:organs) {
                TreeNode rootNode = getTreeNode(rs, null, true,false,null);
                treeNodes.add(rootNode);
            }
        }
        return treeNodes;
    }

    /**
     * 获取所有导航机构.
     *
     * @param parentId       父级ID 为null，则可查询所有节点；不为null，则查询该级以及以下
     * @param excludeOrganId 需要排除的机构ID 子级也会被排除
     * @param isCascade      是否级联
     * @param adduser        是否带用户
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public List<TreeNode> getOrganTree(Long parentId, Long excludeOrganId, boolean isCascade, boolean adduser,List<Long> checkedUserIds) throws DaoException, SystemException,
            ServiceException {
        List<TreeNode> treeNodes = Lists.newArrayList();
        // 顶级机构

        List<Organ> organs = Lists.newArrayList();
        if (parentId == null) {
            organs = getByParentId(null,
                    StatusState.normal.getValue());
        } else {
            Organ parent = this.loadById(parentId);
            organs.add(parent);
        }

        for (Organ rs : organs) {
            TreeNode rootNode = getTreeNode(rs, excludeOrganId, isCascade, adduser,checkedUserIds);
            rootNode.setIconCls("icon-organ-root");
            treeNodes.add(rootNode);
        }
        return treeNodes;

    }



    /**
     *
     * 根据name得到Organ.
     *
     * @param name
     *            机构名称
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public Organ getByName(String name) throws DaoException, SystemException,
            ServiceException {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        name = StringUtils.strip(name);// 去除两边空格
        return organDao.findUniqueBy("name", name);
    }
    /**
     *
     * 根据系统编码得到Organ.
     *
     * @param sysCode
     *            机构系统编码
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public Organ getBySysCode(String sysCode) throws DaoException, SystemException,
            ServiceException {
        if (StringUtils.isBlank(sysCode)) {
            return null;
        }
        return organDao.findUniqueBy("sysCode", sysCode);
    }

    /**
     *
     * 根据编码得到Organ.
     *
     * @param code
     *            机构编码
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public Organ getByCode(String code) throws DaoException, SystemException,
            ServiceException {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return organDao.findUniqueBy("code", code);
    }

    /**
     *
     * 根据父ID得到 Organ. <br>
     * 默认按 orderNo asc,id asc排序.
     *
     * @param parentId
     *            父节点ID(当该参数为null的时候查询顶级机构列表)
     * @param status
     *            数据状态 @see com.eryansky.common.orm.entity.StatusState
     *            <br>status传null则使用默认值 默认值:StatusState.normal.getValue()
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @SuppressWarnings("unchecked")
    public List<Organ> getByParentId(Long parentId, Integer status)
            throws DaoException, SystemException, ServiceException {
        //默认值 正常
        if(status == null){
            status = StatusState.normal.getValue();
        }
        StringBuilder sb = new StringBuilder();
        Parameter parameter = new Parameter();
        sb.append("from Organ o where o.status = :status  ");
        parameter.put("status",status);
        sb.append(" and o.parentOrgan");
        if (parentId == null) {
            sb.append(" is null ");
        } else {
            sb.append(".id = :parentId ");
            parameter.put("parentId",parentId);
        }
        sb.append(" order by o.orderNo asc,o.id asc");

        List<Organ> list = organDao.find(sb.toString(), parameter);
        return list;
    }


    /**
     * 得到排序字段的最大值.
     *
     * @return 返回排序字段的最大值
     */
    public Integer getMaxSort() throws DaoException, SystemException,
            ServiceException {
        Iterator<?> iterator = organDao.createQuery(
                "select max(o.orderNo)from Organ o ").iterate();
        Integer max = 0;
        while (iterator.hasNext()) {
            max = (Integer) iterator.next();
            if (max == null) {
                max = 0;
            }
        }
        return max;
    }

    /**U
     * 查询指定机构以及子机构
     * @param sysCode 机构系统编码
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public List<Organ> findOrgansBySysCode(String sysCode) throws DaoException, SystemException,ServiceException{
        Parameter parameter = new Parameter(StatusState.normal.getValue(),sysCode+"%");
        StringBuilder hql = new StringBuilder();
        hql.append("from Organ o where o.status = :p1  and o.sysCode like  :p2 order by o.sysCode asc");
        List<Organ> list = organDao.find(hql.toString(), parameter);
        return list;
    }

    /**
     * 根据ID查找
     * @param organIds 机构ID集合
     * @return
     */
    public List<Organ> findOrgansByIds(List<Long> organIds) {
        Parameter parameter = new Parameter(organIds);
        return getEntityDao().find("from Organ o where o.id in :p1",parameter);
    }
}
