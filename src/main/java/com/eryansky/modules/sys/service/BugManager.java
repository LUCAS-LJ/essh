/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.modules.sys.entity.Bug;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * bug管理Service层.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-27 下午8:00:22 
 *
 */
@Service
public class BugManager extends EntityManager<Bug, Long> {

	private HibernateDao<Bug, Long> bugDao;
	
	
	/**
	 * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
	 */
	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		bugDao = new HibernateDao<Bug, Long>(sessionFactory, Bug.class);
	}
	
	@Override
	protected HibernateDao<Bug, Long> getEntityDao() {
		return bugDao;
	}

}
