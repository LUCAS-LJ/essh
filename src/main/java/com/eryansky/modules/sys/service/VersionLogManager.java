/**
 *  Copyright (c) 2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.modules.sys.entity.VersionLog;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 温春平@wencp wencp@jx.tobacco.gov.cn
 * @date 2015-01-09
 */
@Service
public class VersionLogManager extends EntityManager<VersionLog, Long> {

    private HibernateDao<VersionLog, Long> versionLogDao;


    /**
     * 通过注入的sessionFactory初始化默认的泛型DAO成员变量.
     */
    @Autowired
    public void setSessionFactory(final SessionFactory sessionFactory) {
        versionLogDao = new HibernateDao<VersionLog, Long>(sessionFactory, VersionLog.class);
    }

    @Override
    protected HibernateDao<VersionLog, Long> getEntityDao() {
        return versionLogDao;
    }

    /**
     * 清空所有更新日志数据
     */
    public void removeAll(){
        int reslutCount = getEntityDao().batchExecute("delete from VersionLog");
        logger.debug("清空版本更新日志：{}",reslutCount);
    }
}
