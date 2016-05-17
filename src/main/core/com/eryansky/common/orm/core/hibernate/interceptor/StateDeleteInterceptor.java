package com.eryansky.common.orm.core.hibernate.interceptor;

import com.eryansky.common.orm.annotation.StateDelete;
import com.eryansky.common.orm.core.hibernate.support.BasicHibernateDao;
import com.eryansky.common.orm.interceptor.OrmDeleteInterceptor;
import com.eryansky.common.utils.ConvertUtils;
import com.eryansky.common.utils.reflection.ReflectionUtils;

import java.io.Serializable;

/**
 * 状态删除拦截器
 * 
 * @author maurice
 *
 * @param <E> 持久化对象类型
 * @param <ID> id主键类型
 */
public class StateDeleteInterceptor<E,ID extends Serializable> implements OrmDeleteInterceptor<E, BasicHibernateDao<E,ID>> {

	/*
	 * (non-Javadoc)
	 * @see OrmDeleteInterceptor#onDelete(java.io.Serializable, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean onDelete(Serializable id, E entity,BasicHibernateDao<E, ID> persistentContext) {
		
		Class<?> entityClass = ReflectionUtils.getTargetClass(entity);
		StateDelete stateDelete = ReflectionUtils.getAnnotation(entityClass,StateDelete.class);
		if (stateDelete == null) {
			return Boolean.TRUE;
		}
		
		Object value = ConvertUtils.convertToObject(stateDelete.value(), stateDelete.type().getValue());
		ReflectionUtils.invokeSetter(entity, stateDelete.propertyName(), value);
		persistentContext.update(entity);
		
		return Boolean.FALSE;
	}

	/*
	 * (non-Javadoc)
	 * @see OrmDeleteInterceptor#onPostDelete(java.io.Serializable, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void onPostDelete(Serializable id, E entity,BasicHibernateDao<E, ID> persistentContext) {
		
	}

}
