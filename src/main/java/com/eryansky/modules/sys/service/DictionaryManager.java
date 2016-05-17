/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.model.Combobox;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.modules.sys.entity.Dictionary;
import com.eryansky.modules.sys.entity.DictionaryType;
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
 * 数据字典实现类.
 * 
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2013-1-24 下午3:01:27
 */
@Service
public class DictionaryManager extends EntityManager<Dictionary, Long> {

	private HibernateDao<Dictionary, Long> dictionaryDao;
    @Autowired
    private DictionaryTypeManager dictionaryTypeManager;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		dictionaryDao = new HibernateDao<Dictionary, Long>(sessionFactory,
				Dictionary.class);
	}

	@Override
	protected HibernateDao<Dictionary, Long> getEntityDao() {
		return dictionaryDao;
	}

	/**
	 * 新增或修改 保存对象.
	 */
    @CacheEvict(value = { CacheConstants.DICTIONARYS_BY_TYPE_CACHE,
            CacheConstants.DICTIONARYS_CONBOTREE_BY_TYPE_CACHE,
            CacheConstants.DICTIONARYS_CONBOBOX_BY_TYPE_CACHE},allEntries = true)
	public void saveOrUpdate(Dictionary entity) throws DaoException, SystemException,
			ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.DICTIONARYS_BY_TYPE_CACHE
                +","+CacheConstants.DICTIONARYS_CONBOTREE_BY_TYPE_CACHE
                +","+CacheConstants.DICTIONARYS_CONBOBOX_BY_TYPE_CACHE);
        Assert.notNull(entity, "参数[entity]为空!");
		dictionaryDao.saveOrUpdate(entity);
	}
	
	/**
	 * 新增或修改 保存对象.
	 */
    @CacheEvict(value = { CacheConstants.DICTIONARYS_BY_TYPE_CACHE,
            CacheConstants.DICTIONARYS_CONBOTREE_BY_TYPE_CACHE,
            CacheConstants.DICTIONARYS_CONBOBOX_BY_TYPE_CACHE},allEntries = true)
	public void merge(Dictionary entity) throws DaoException, SystemException,
			ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.DICTIONARYS_BY_TYPE_CACHE
                +","+CacheConstants.DICTIONARYS_CONBOTREE_BY_TYPE_CACHE
                +","+CacheConstants.DICTIONARYS_CONBOBOX_BY_TYPE_CACHE);
        Assert.notNull(entity, "参数[entity]为空!");
		dictionaryDao.merge(entity);
	}

    @CacheEvict(value = { CacheConstants.DICTIONARYS_BY_TYPE_CACHE,
            CacheConstants.DICTIONARYS_CONBOTREE_BY_TYPE_CACHE,
            CacheConstants.DICTIONARYS_CONBOBOX_BY_TYPE_CACHE},allEntries = true)
    @Override
    public void saveEntity(Dictionary entity) throws DaoException, SystemException, ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.DICTIONARYS_BY_TYPE_CACHE
                +","+CacheConstants.DICTIONARYS_CONBOTREE_BY_TYPE_CACHE
                +","+CacheConstants.DICTIONARYS_CONBOBOX_BY_TYPE_CACHE);
        super.saveEntity(entity);
    }

    @CacheEvict(value = { CacheConstants.DICTIONARYS_BY_TYPE_CACHE,
            CacheConstants.DICTIONARYS_CONBOTREE_BY_TYPE_CACHE,
            CacheConstants.DICTIONARYS_CONBOBOX_BY_TYPE_CACHE},allEntries = true)
    @Override
    public void deleteByIds(List<Long> ids) throws DaoException, SystemException, ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.DICTIONARYS_BY_TYPE_CACHE
                +","+CacheConstants.DICTIONARYS_CONBOTREE_BY_TYPE_CACHE
                +","+CacheConstants.DICTIONARYS_CONBOBOX_BY_TYPE_CACHE);
        super.deleteByIds(ids);
    }

	/**
	 * 根据编码code得到对象.
	 * 
	 * @param code
	 *            数据字典编码
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public Dictionary getByCode(String code) throws DaoException,
			SystemException, ServiceException {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		code = StringUtils.strip(code);// 去除两边空格
        Parameter parameter = new Parameter(code);
		List<Dictionary> list = getEntityDao().find(
				"from Dictionary d where d.code = :p1",parameter);
		return list.isEmpty() ? null : list.get(0);
	}

	/**
	 * 根据数据字典类型编码dictionaryTypeCode得到List<TreeNode>对象. <br>
	 * 当id不为空的时候根据id排除自身节点.
	 *
	 * @param excludeDictionaryId
	 *            需要排除数据字典ID 下级也会被排除
	 * @param isCascade
	 *            是否级联加载
	 * @return List<TreeNode> 映射关系： TreeNode.text-->Dicitonary.text;TreeNode.id-->Dicitonary.code;
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
    @Cacheable(value = { CacheConstants.DICTIONARYS_CONBOTREE_BY_TYPE_CACHE} )
	@SuppressWarnings("unchecked")
	public List<TreeNode> getByDictionaryTypeCode(String dictionaryTypeCode, Long excludeDictionaryId, boolean isCascade)
			throws DaoException, SystemException, ServiceException {
        logger.debug("缓存:{}", CacheConstants.DICTIONARYS_CONBOTREE_BY_TYPE_CACHE);
        List<Dictionary> list = null;
		List<TreeNode> treeNodes = Lists.newArrayList();
		if (StringUtils.isBlank(dictionaryTypeCode)) {
			return treeNodes;
		}
		StringBuilder hql = new StringBuilder();
        Parameter parameter = new Parameter(StatusState.normal.getValue(),dictionaryTypeCode);
		hql.append("from Dictionary d where d.status = :p1 and d.dictionaryType.code = :p2  and d.parentDictionary is null order by d.orderNo");
		logger.debug(hql.toString());
		list = getEntityDao().find(hql.toString(), parameter);
        for (Dictionary d : list) {
            TreeNode t = getTreeNode(d, excludeDictionaryId, isCascade);
            if (t != null) {
                treeNodes.add(t);
            }

		}
		return treeNodes;
	}

	/**
	 * /** 根据数据字典类型编码dictionaryTypeCode得到List<TreeNode>对象. <br>
	 * 当id不为空的时候根据id排除自身节点.
	 *
	 * @param entity
	 *            数据字典对象
	 * @param excludeDictionaryId
	 *            数据字ID
	 * @param isCascade
	 *            是否级联加载
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public TreeNode getTreeNode(Dictionary entity, Long excludeDictionaryId, boolean isCascade)
			throws DaoException, SystemException, ServiceException {
		TreeNode node = new TreeNode(entity.getCode(), entity.getName());
//        node.getAttributes().put("code",entity.getCode());
		// Map<String, Object> attributes = new HashMap<String, Object>();
		// node.setAttributes(attributes);
		List<Dictionary> subDictionaries = getByParentCode(entity.getCode());
        if (isCascade) {// 递归查询子节点
            List<TreeNode> children = Lists.newArrayList();
            for (Dictionary d : subDictionaries) {
                boolean isInclude = true;// 是否包含到节点树
                TreeNode treeNode = null;
                // 排除自身
                if (excludeDictionaryId != null) {
                    if (!d.getId().equals(excludeDictionaryId)) {
                        treeNode = getTreeNode(d, excludeDictionaryId, true);
                    } else {
                        isInclude = false;
                    }
                } else {
                    treeNode = getTreeNode(d, excludeDictionaryId, true);
                }
                if (isInclude) {
                    children.add(treeNode);
                    node.setState(TreeNode.STATE_CLOASED);
                } else {
                    node.setState(TreeNode.STATE_OPEN);
                }
            }

            node.setChildren(children);
        }
		return node;
	}

    /**
     * @param dictionaryTypeCode 字典类型编码
     * @param dictionaryCode 字典项编码
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public Dictionary getDictionaryByDC(
            String dictionaryTypeCode,String dictionaryCode) throws DaoException, SystemException,
            ServiceException {
        Assert.notNull(dictionaryTypeCode, "参数[dictionaryTypeCode]为空!");
        Assert.notNull(dictionaryCode, "参数[dictionaryCode]为空!");
        Parameter parameter = new Parameter(dictionaryTypeCode,dictionaryCode);
        List<Dictionary> list = getEntityDao().find(
                "from Dictionary d where d.dictionaryType.code = :p1 and d.code = :p2 ",parameter);
        return list.isEmpty() ? null:list.get(0);
    }

    /**
     * @param dictionaryTypeCode 字典类型编码
     * @param dictionaryValue 字典项值
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public Dictionary getDictionaryByDV(
            String dictionaryTypeCode,String dictionaryValue) throws DaoException, SystemException,
            ServiceException {
        Assert.notNull(dictionaryTypeCode, "参数[dictionaryTypeCode]为空!");
//        Assert.notNull(dictionaryValue, "参数[dictionaryValue]为空!");
        Parameter parameter = new Parameter(dictionaryTypeCode,dictionaryValue);
        List<Dictionary> list = getEntityDao().find(
                "from Dictionary d where d.dictionaryType.code = :p1 and d.value = :p2 ",parameter);
        return list.isEmpty() ? null:list.get(0);
    }

	/**
	 * 根据数据字典类型编码得到数据字典列表.
	 *
	 * @param dictionaryTypeCode 字典分类编码
	 * @return
	 * @throws DaoException
	 *             ,SystemException,ServiceException
	 */
    @Cacheable(value = { CacheConstants.DICTIONARYS_BY_TYPE_CACHE})
	@SuppressWarnings("unchecked")
	public List<Dictionary> getDictionarysByDictionaryTypeCode(
			String dictionaryTypeCode) throws DaoException, SystemException,
			ServiceException {
        Assert.notNull(dictionaryTypeCode, "参数[dictionaryTypeCode]为空!");
        Parameter parameter = new Parameter(dictionaryTypeCode);
        List<Dictionary> list = getEntityDao().find(
				"from Dictionary d where d.dictionaryType.code = :p1 order by d.orderNo",parameter);
        logger.debug("缓存:{}", CacheConstants.DICTIONARYS_BY_TYPE_CACHE+" 参数：dictionaryTypeCode="+dictionaryTypeCode);
        return list;
	}

	/**
	 * Combobox下拉框数据.
	 *
	 * @param dictionaryTypeCode
	 *            数据字典类型编码
	 * @return List<Combobox> 映射关系： Combobox.text-->Dicitonary.text;Combobox.value-->Dicitonary.value;
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
    @Cacheable(value = { CacheConstants.DICTIONARYS_CONBOBOX_BY_TYPE_CACHE})
	public List<Combobox> getByDictionaryTypeCode(String dictionaryTypeCode)
			throws DaoException, SystemException, ServiceException {
		List<Dictionary> list = getDictionarysByDictionaryTypeCode(dictionaryTypeCode);
        List<Combobox> cList = Lists.newArrayList();
        for (Dictionary d : list) {
            Combobox c = new Combobox(d.getValue(), d.getName());
            cList.add(c);
        }
        logger.debug("缓存:{}", CacheConstants.DICTIONARYS_CONBOBOX_BY_TYPE_CACHE+" 参数：dictionaryTypeCode="+dictionaryTypeCode);
        return cList;

	}

	/**
	 * 根据父ID得到list对象.
	 *
	 * @param parentCode
	 *            父级编码
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public List<Dictionary> getByParentCode(String parentCode)
			throws DaoException, SystemException, ServiceException {
		StringBuilder sb = new StringBuilder();
        Parameter parameter = new Parameter();
		sb.append("from Dictionary d where d.status = :status ");
        parameter.put("status",StatusState.normal.getValue());
		if (parentCode == null) {
			sb.append(" and d.parentDictionary is null ");
		} else {
			sb.append(" and d.parentDictionary.code  = :parentCode ");
            parameter.put("parentCode",parentCode);
		}
		sb.append(" order by d.orderNo");
		List<Dictionary> list = getEntityDao().find(sb.toString(), parameter);
		return list;
	}

	/**
	 * 得到排序字段的最大值.
	 *
	 * @return 返回排序字段的最大值
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	public Integer getMaxSort() throws DaoException, SystemException,
			ServiceException {
		Iterator<?> iterator = dictionaryDao.createQuery(
				"select max(d.orderNo)from Dictionary d ").iterate();
		Integer max = null;
		while (iterator.hasNext()) {
			// Object[] row = (Object[]) iterator.next();
			max = (Integer) iterator.next();
		}
		if (max == null) {
			max = 0;
		}
		return max;
	}

    /**
     * 根据字典类型编码 查找
     * @param groupDictionaryTypeCode 字典分类分组编码
     * @return Map<String, List<Dictionary>> key:分类编码（即子类编码） value: 数据字典项集合List<Dictionary>
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public Map<String, List<Dictionary>> getDictionaryTypesByGroupDictionaryTypeCode(String groupDictionaryTypeCode)
            throws DaoException, SystemException,ServiceException {
        Map<String, List<Dictionary>> map = Maps.newHashMap();
        DictionaryType dictionaryType = dictionaryTypeManager.getByCode(groupDictionaryTypeCode);
        for (DictionaryType subDictionaryType : dictionaryType.getSubDictionaryTypes()) {
            List<Dictionary> dictionaries = this.getDictionarysByDictionaryTypeCode(subDictionaryType.getCode());
            map.put(subDictionaryType.getCode(), dictionaries);
        }
        return map;
    }
}
