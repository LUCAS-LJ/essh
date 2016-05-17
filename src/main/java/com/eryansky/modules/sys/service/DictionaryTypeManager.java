/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys.entity.DictionaryType;
import com.eryansky.utils.CacheConstants;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.List;

/**
 * 数据字典类型实现类.
 * 
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2013-1-24 下午3:01:27
 */
@Service
public class DictionaryTypeManager extends
		EntityManager<DictionaryType, Long> {

	private HibernateDao<DictionaryType, Long> dictionaryTypeDao;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		dictionaryTypeDao = new HibernateDao<DictionaryType, Long>(
				sessionFactory, DictionaryType.class);
	}

	@Override
	protected HibernateDao<DictionaryType, Long> getEntityDao() {
		return dictionaryTypeDao;
	}

    @CacheEvict(value = { CacheConstants.DICTIONARY_TYPE_ALL_CACHE,
            CacheConstants.DICTIONARY_TYPE_GROUPS_CACHE},allEntries = true)
	public void saveOrUpdate(DictionaryType entity) throws DaoException,
			SystemException, ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.DICTIONARY_TYPE_ALL_CACHE
                +","+CacheConstants.DICTIONARY_TYPE_GROUPS_CACHE);
        Assert.notNull(entity, "参数[entity]为空!");
		dictionaryTypeDao.saveOrUpdate(entity);
	}

    @CacheEvict(value = { CacheConstants.DICTIONARY_TYPE_ALL_CACHE,
            CacheConstants.DICTIONARY_TYPE_GROUPS_CACHE},allEntries = true)
	public void deleteByIds(List<Long> ids) throws DaoException, SystemException,
			ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.DICTIONARY_TYPE_ALL_CACHE
                +","+CacheConstants.DICTIONARY_TYPE_GROUPS_CACHE);
        if(!Collections3.isEmpty(ids)){
            for (Long id:ids) {
                DictionaryType dictionaryType = this.getById(id);
                List<DictionaryType> subDictionaryTypes = dictionaryType.getSubDictionaryTypes();
                if (!subDictionaryTypes.isEmpty()) {
                    dictionaryTypeDao.deleteAll(subDictionaryTypes);
                }
                dictionaryTypeDao.delete(dictionaryType);
            }
        }else{
            logger.warn("参数[ids]为空.");
        }
	}


    @CacheEvict(value = { CacheConstants.DICTIONARY_TYPE_ALL_CACHE,
            CacheConstants.DICTIONARY_TYPE_GROUPS_CACHE},allEntries = true)
    @Override
    public void saveEntity(DictionaryType entity) throws DaoException, SystemException, ServiceException {
        logger.debug("清空缓存:{}", CacheConstants.DICTIONARY_TYPE_ALL_CACHE
                +","+CacheConstants.DICTIONARY_TYPE_GROUPS_CACHE);
        super.saveEntity(entity);
    }

    /**
	 * 根据名称name得到对象.
	 * 
	 * @param name
	 *            数据字典名称
	 * @return
	 * @throws DaoException
	 * @throws SystemException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public DictionaryType getByName(String name) throws DaoException,
			SystemException, ServiceException {
		if (StringUtils.isBlank(name)) {
			return null;
		}
		name = StringUtils.strip(name);// 去除两边空格
        Parameter parameter = new Parameter(name);
		List<DictionaryType> list = getEntityDao().find("from DictionaryType d where d.name = :p1",parameter);
		return list.isEmpty() ? null : list.get(0);
	}

    /**
     * 根据分组编码以及名称查找对象
     * @param groupDictionaryTypeCode 分组类型编码
     * @param name   类型名称
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    public DictionaryType getByGroupCode_Name(String groupDictionaryTypeCode,String name) throws DaoException,
            SystemException, ServiceException {
        Assert.notNull(groupDictionaryTypeCode, "参数[groupDictionaryTypeCode]为空!");
        Assert.notNull(name, "参数[name]为空!");
        Parameter parameter = new Parameter(groupDictionaryTypeCode,name);
        List<DictionaryType> list = getEntityDao().find("from DictionaryType d where d.groupDictionaryType.code = :p1 and d.name = :p2",parameter);
        return list.isEmpty() ? null : list.get(0);
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
	public DictionaryType getByCode(String code) throws DaoException,
			SystemException, ServiceException {
		if (StringUtils.isBlank(code)) {
			return null;
		}
		code = StringUtils.strip(code);// 去除两边空格
        Parameter parameter = new Parameter(code);
		List<DictionaryType> list = getEntityDao().find("from DictionaryType d where d.code = :p1",parameter);
		return list.isEmpty() ? null : list.get(0);
	}


    @Cacheable(value = { CacheConstants.DICTIONARY_TYPE_ALL_CACHE})
	public List<DictionaryType> getAll() throws DaoException, SystemException,
			ServiceException {
		logger.debug("缓存:{}", CacheConstants.DICTIONARY_TYPE_ALL_CACHE);
		return dictionaryTypeDao.getAll();
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
		Iterator<?> iterator = dictionaryTypeDao.createQuery(
				"select max(d.orderNo)from DictionaryType d ").iterate();
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
     * 查找所有分组列表.
     * @return
     * @throws DaoException
     * @throws SystemException
     * @throws ServiceException
     */
    @Cacheable(value = {CacheConstants.DICTIONARY_TYPE_GROUPS_CACHE})
    public List<DictionaryType> getGroupDictionaryTypes() throws DaoException, SystemException,
            ServiceException {
        List<DictionaryType> list = dictionaryTypeDao.find(
                "from DictionaryType d where d.groupDictionaryType is null");
        logger.debug("缓存:{}", CacheConstants.DICTIONARY_TYPE_GROUPS_CACHE);
        return list;
    }


}
