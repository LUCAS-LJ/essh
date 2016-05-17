/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.notice.service;

import com.eryansky.common.exception.DaoException;
import com.eryansky.common.exception.ServiceException;
import com.eryansky.common.exception.SystemException;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.modules.notice._enum.NoticeMode;
import com.eryansky.modules.notice._enum.NoticeReadMode;
import com.eryansky.modules.notice.entity.NoticeScope;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.List;

/**
 * service
 *
 * @author 温春平&wencp wencp@jx.tobacco.gov.cn
 */
@Service
public class NoticeScopeManager extends EntityManager<NoticeScope, Long> {

    private HibernateDao<NoticeScope, Long> noticeScopeDao;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        noticeScopeDao = new HibernateDao<NoticeScope, Long>(sessionFactory, NoticeScope.class);
    }

    @Override
    protected HibernateDao<NoticeScope, Long> getEntityDao() {
        return noticeScopeDao;
    }

    /**
     * 查看某个通知阅读情况
     * @param page
     * @param noticeId 通知ID
     * @return
     * @throws com.eryansky.common.exception.SystemException
     * @throws com.eryansky.common.exception.ServiceException
     * @throws com.eryansky.common.exception.DaoException
     */
    public Page<NoticeScope> findReadInfoPage(Page<NoticeScope> page, Long noticeId)
            throws SystemException, ServiceException, DaoException {
        Assert.notNull(noticeId, "参数[noticeId]为空!");
        StringBuilder hql = new StringBuilder();
        Parameter parameter = new Parameter(noticeId);
        hql.append("from NoticeScope s where  s.noticeId = :p1");
        return getEntityDao().findPage(page,hql.toString(),parameter);
    }

    /**
     * 根据通知ID查找
     * @param noticeId 通知ID
     * @return
     */
    public List<NoticeScope> getNoticeScopesByNoticeId(Long noticeId) {
        return getNoticeScopesByNoticeId(noticeId,null);
    }

    /**
     * 根据通知ID查找
     * @param noticeId 通知ID
     * @param userId 用户ID 可为null
     * @return
     */
    public List<NoticeScope> getNoticeScopesByNoticeId(Long noticeId, Long userId) {
        Parameter parameter = new Parameter(noticeId);
        StringBuffer hql = new StringBuffer();
        hql.append("from NoticeScope s where s.noticeId = :p1");
        if(userId != null){
            hql.append(" and s.userId = :userId");
            parameter.put("userId",userId);
        }
        return getEntityDao().find(hql.toString(), parameter);
    }

    /**
     * 获取用户最近通知列表
     * @param userId 用户ID
     * @param maxSize 数量 默认值：{@link Page.DEFAULT_PAGESIZE}
     * @return
     * @throws SystemException
     * @throws ServiceException
     * @throws DaoException
     */
    public List<NoticeScope> getUserNewNotices(Long userId, Integer maxSize) throws SystemException,
            ServiceException, DaoException {
        StringBuilder hql = new StringBuilder();
        Parameter parameter = new Parameter(userId, StatusState.normal.getValue(), NoticeMode.Effective.getValue());
        hql.append("select s from Notice n,NoticeScope s where s.noticeId = n.id and s.userId = :p1 and n.status = :p2 and n.noticeMode = :p3")
                .append(" order by n.publishTime desc,n.isTop desc,s.isRead asc");
        Query query = getEntityDao().createQuery(hql.toString(), parameter);
        query.setFirstResult(0);
        query.setMaxResults(maxSize == null ? Page.DEFAULT_PAGESIZE:maxSize);
        List<NoticeScope> list = query.list();
        return list;

    }

    /**
     * 用户未读通知数量
     * @param userId 用户ID
     * @return
     * @throws SystemException
     * @throws ServiceException
     * @throws DaoException
     */
    public long getUserUnreadNoticeNum(Long userId)throws SystemException,ServiceException, DaoException {
        StringBuilder hql = new StringBuilder();
        Parameter parameter = new Parameter(userId, NoticeReadMode.unreaded.getValue(), StatusState.normal.getValue(), NoticeMode.Effective.getValue());
        hql.append("select count(s.id) from Notice n,NoticeScope s where s.noticeId = n.id and s.userId = :p1 and s.isRead = :p2 and n.status = :p3 and n.noticeMode = :p4");
        List<Object> list = getEntityDao().find(hql.toString(),parameter);
        return (Long)list.get(0);
    }


    /**
     * 用户是否阅读通知
     * @param userId  用户ID
     * @param noticeId 通知ID
     * @return
     * @throws SystemException
     * @throws ServiceException
     * @throws DaoException
     */
    public boolean isRead(Long userId, Long noticeId) throws SystemException,
            ServiceException, DaoException {
        Parameter parameter = new Parameter(userId, noticeId, NoticeReadMode.readed.getValue());
        StringBuffer hql = new StringBuffer();
        hql.append("select count(*) from NoticeScope s where s.userId = :p1 and s.noticeId = :p2 and s.isRead = :p3");
        List<Object> list = getEntityDao().find(hql.toString(),parameter);
        Long count = (Long)list.get(0);
        if (count > 0L) {
            return true;
        }
        return false;
    }

    /**
     * 设置已读通知
     *
     * @param userId
     * @param noticeId
     */
    public synchronized int setRead(Long userId, Long noticeId) {
        Parameter parameter = new Parameter(NoticeReadMode.readed.getValue(), Calendar.getInstance().getTime(), noticeId, userId);
        return getEntityDao().createQuery("update NoticeScope s set s.isRead = :p1,s.readTime = :p2 where s.noticeId = :p3 and s.userId= :p4", parameter).executeUpdate();
    }


}
