package com.eryansky.core.datasource;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import javax.sql.DataSource;

/**
 * 重写HibernateTransactionManager事务管理器，实现自己的动态的事务管理器
 */
public class DynamicTransactionManager extends HibernateTransactionManager {
 
    private static final long serialVersionUID = -4655721479296819154L;
    
    /** 
     * @see org.springframework.orm.hibernate4.HibernateTransactionManager#getDataSource()
     */
    @Override
    public DataSource getDataSource() {
        return SessionFactoryUtils.getDataSource(getSessionFactory());
    }
 
    /** 
     * @see org.springframework.orm.hibernate4.HibernateTransactionManager#getSessionFactory()
     */
    @Override
    public SessionFactory getSessionFactory() {
        DynamicSessionFactory dynamicSessionFactory = (DynamicSessionFactory) super.getSessionFactory();
        SessionFactory hibernateSessionFactory = dynamicSessionFactory.getHibernateSessionFactory();
        return hibernateSessionFactory;  
    }
}