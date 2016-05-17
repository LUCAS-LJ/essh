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
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.disk.utils.DiskUtils;
import com.eryansky.modules.notice._enum.IsTop;
import com.eryansky.modules.notice._enum.NoticeMode;
import com.eryansky.modules.notice.entity.Notice;
import com.eryansky.modules.notice.entity.NoticeScope;
import com.eryansky.modules.notice.vo.NoticeQueryVo;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.OrganManager;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.utils.YesOrNo;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * service
 * 
 * @author xush
 */
@Service
public class NoticeManager extends EntityManager<Notice, Long> {

	@Autowired
	private UserManager userManager;
	@Autowired
	private OrganManager organManager;
	@Autowired
	private NoticeScopeManager noticeScopeManager;


    private HibernateDao<Notice, Long> noticeDao;

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		noticeDao = new HibernateDao<Notice, Long>(sessionFactory, Notice.class);
	}

	@Override
	protected HibernateDao<Notice, Long> getEntityDao() {
		return noticeDao;
	}

    /**
     * 删除通知
     * @param noticeId
     */
    public void removeNotice(Long noticeId){
        Notice notice = this.loadById(noticeId);
        if(notice != null){
            List<NoticeScope> noticeScopes = noticeScopeManager.getNoticeScopesByNoticeId(noticeId,null);
            noticeScopeManager.deleteAll(noticeScopes);
            List<Long> fileIds = notice.getFileIds();
            if (Collections3.isNotEmpty(fileIds)) {
                for (Long fileId : fileIds) {
                    DiskUtils.deleteFile(null, fileId);
                }
            }
            this.delete(notice);
        }
    }

    /**
     * 属性过滤器查找得到分页数据.
     *
     * @param page 分页对象
     * @param userId 发布人 查询所有则传null
     * @param noticeQueryVo 标查询条件
     * @return
     * @throws SystemException
     * @throws ServiceException
     * @throws DaoException
     */
	public Page<Notice> findPage(Page<Notice> page, Long userId, NoticeQueryVo noticeQueryVo)
			throws SystemException, ServiceException, DaoException {
        StringBuilder hql = new StringBuilder();
        Parameter parameter = new Parameter(StatusState.normal.getValue());
        hql.append(" select n from Notice n where n.status = :p1");
        if(userId != null){
            hql.append(" and n.userId = :userId");
            parameter.put("userId",userId);
        }

        if(noticeQueryVo != null){
            if (noticeQueryVo.getIsTop() != null) {
                hql.append(" and n.isTop = :isTop");
                parameter.put("isTop", noticeQueryVo.getIsTop());
            }
            if (noticeQueryVo.getIsRead() != null) {
                hql.append(" and n.isRead = :isRead");
                parameter.put("isRead", noticeQueryVo.getIsRead());
            }

            if (StringUtils.isNotBlank(noticeQueryVo.getTitle())) {
                hql.append(" and n.title like :title");
                parameter.put("title","%" + noticeQueryVo.getTitle() + "%");
            }
            if (StringUtils.isNotBlank(noticeQueryVo.getContent())) {
                hql.append(" and n.content like :content");
                parameter.put("content","%" + noticeQueryVo.getContent() + "%");
            }
            if (Collections3.isNotEmpty(noticeQueryVo.getPublishUserIds())) {
                hql.append(" and n.userId in (:publishUserIds)");
                parameter.put("publishUserIds", noticeQueryVo.getPublishUserIds());
            }

            if (noticeQueryVo.getStartTime() != null && noticeQueryVo.getEndTime() != null) {
                hql.append(" and  (n.publishTime between :startTime and :endTime)");
                parameter.put("startTime", noticeQueryVo.getStartTime());
                parameter.put("endTime", noticeQueryVo.getEndTime());
            }
        }

        hql.append(" order by n.publishTime desc");
        return noticeDao.findPage(page,hql.toString(),parameter);

	}

    /**
     * 我的邮件 分页查询.
     * @param page
     * @param userId 用户ID
     * @param noticeQueryVo 查询条件
     * @return
     * @throws SystemException
     * @throws ServiceException
     * @throws DaoException
     */
	public Page<NoticeScope> findReadNoticePage(Page<NoticeScope> page, Long userId, NoticeQueryVo noticeQueryVo) throws SystemException,
            ServiceException, DaoException {
		Assert.notNull(userId, "参数[userId]为空!");
		StringBuilder hql = new StringBuilder();
        Parameter parameter = new Parameter(userId, StatusState.normal.getValue(),NoticeMode.Effective.getValue());
		hql.append("select s from Notice n,NoticeScope s where s.noticeId = n.id and s.userId = :p1 and n.status = :p2 and n.noticeMode = :p3");
        if(noticeQueryVo != null){
            if (noticeQueryVo.getIsTop() != null) {
                hql.append(" and n.isTop = :isTop");
                parameter.put("isTop", noticeQueryVo.getIsTop());
            }
            if (noticeQueryVo.getIsRead() != null) {
                hql.append(" and n.isRead = :isRead");
                parameter.put("isRead", noticeQueryVo.getIsRead());
            }

            if (StringUtils.isNotBlank(noticeQueryVo.getTitle())) {
                hql.append(" and n.title like :title");
                parameter.put("title","%" + noticeQueryVo.getTitle() + "%");
            }
            if (StringUtils.isNotBlank(noticeQueryVo.getContent())) {
                hql.append(" and n.content like :content");
                parameter.put("content","%" + noticeQueryVo.getContent() + "%");
            }
            if (Collections3.isNotEmpty(noticeQueryVo.getPublishUserIds())) {
                hql.append(" and n.userId in (:publishUserIds)");
                parameter.put("publishUserIds", noticeQueryVo.getPublishUserIds());
            }

            if (noticeQueryVo.getStartTime() != null && noticeQueryVo.getEndTime() != null) {
                hql.append(" and  (n.publishTime between :startTime and :endTime)");
                parameter.put("startTime", noticeQueryVo.getStartTime());
                parameter.put("endTime", noticeQueryVo.getEndTime());
            }
        }

		hql.append(" order by n.publishTime desc");
        return noticeScopeManager.findPage(page,hql.toString(),parameter);
	}

    /**
     * 发布公告
     *
     * @param noticeId
     *            公告ID
     */
    public void publish(Long noticeId) {
        Notice notice = this.loadById(noticeId);
        if (notice == null) {
            throw new ServiceException("公告[" + noticeId + "]不存在.");
        }
        publish(notice);
    }

	/**
	 * 发布公告
	 * 
	 * @param notice 通知
	 */
	public void publish(Notice notice) {
        Date nowTime = Calendar.getInstance().getTime();
        //已经发布过 删除接收对象记录
        if(NoticeMode.Effective.getValue().equals(notice.getNoticeMode())){
            List<NoticeScope> noticeScopes = noticeScopeManager.getNoticeScopesByNoticeId(notice.getId(),null);
            noticeScopeManager.deleteAll(noticeScopes);
        }

        notice.setNoticeMode(NoticeMode.Effective.getValue());
        notice.setPublishTime(nowTime);
		this.saveOrUpdate(notice);
        if(YesOrNo.YES.getValue().equals(notice.getIsToAll())){
            List<User> userList = userManager.getAllNormal();
            for(User user:userList){
                NoticeScope noticeScope = new NoticeScope();
                noticeScope.setNoticeId(notice.getId());
                noticeScope.setUserId(user.getId());
                noticeScope.setOrganId(user.getDefaultOrganId());
                noticeScopeManager.save(noticeScope);
            }

        }else{
            if (Collections3.isNotEmpty(notice.getNoticeOrganIds())) {
                for (Long organId : notice.getNoticeOrganIds()) {
                    List<User> users = organManager.getById(organId).getUsers();
                    if (Collections3.isNotEmpty(users)) {
                        for (User user : users) {
                            List<NoticeScope> userNoticeScopes = noticeScopeManager.getNoticeScopesByNoticeId(notice.getId(),user.getId());
                            if(Collections3.isNotEmpty(userNoticeScopes)){
                                break;
                            }
                            NoticeScope noticeScope = new NoticeScope();
                            noticeScope.setNoticeId(notice.getId());
                            noticeScope.setUserId(user.getId());
                            noticeScope.setOrganId(organId);
                            noticeScopeManager.save(noticeScope);
                        }
                    }
                }
            }

            if (Collections3.isNotEmpty(notice.getNoticeUserIds())) {
                for (Long userId : notice.getNoticeUserIds()) {
                    List<NoticeScope> userNoticeScopes = noticeScopeManager.getNoticeScopesByNoticeId(notice.getId(),userId);
                    if(Collections3.isNotEmpty(userNoticeScopes)){
                        break;
                    }
                    User user = userManager.getById(userId);
                    NoticeScope noticeScope = new NoticeScope();
                    noticeScope.setNoticeId(notice.getId());
                    noticeScope.setUserId(userId);
                    noticeScope.setOrganId(user.getDefaultOrganId());
                    noticeScopeManager.save(noticeScope);
                }
            }
        }

	}

    public void saveFromModel(Notice notice,boolean isPublish){
        this.saveEntity(notice);
        if(isPublish){
            this.publish(notice.getId());
        }
    }

    /**
     * 标记为已读
     * @param userId 所属用户ID
     * @param noticeIds 通知ID集合
     */
    public void markReaded(Long userId,List<Long> noticeIds){
        if (Collections3.isNotEmpty(noticeIds)) {
            for (Long id : noticeIds) {
                noticeScopeManager.setRead(userId,id);
            }
        } else {
            logger.warn("参数[entitys]为空.");
        }

    }

    
    /**
     * 虚拟删除通知
     * @param ids
     */
	public void remove(List<Long> ids) {
		if (Collections3.isNotEmpty(ids)) {
			for (Long id : ids) {
				Notice notice = getById(id);
				notice.setStatus(StatusState.delete.getValue());
				saveEntity(notice);
			}
		}
	}


    /**
     * 查找通知附件ID
     * @param noticeId
     * @return
     */
    public List<Long> getFileIds(Long noticeId){
        List<Notice> list = getEntityDao().find("select n from Notice n where n.id = :p1",new Parameter(noticeId));
        Notice notice = list.isEmpty() ? null:list.get(0);
        if(notice !=null){
            return notice.getFileIds();
        }
        return null;
    }

    /**
     * 轮询通知 定时发布、到时失效、取消置顶
     * @throws SystemException
     * @throws ServiceException
     * @throws DaoException
     */
    public void pollNotice() throws SystemException, ServiceException,
            DaoException {
        Parameter parameter = new Parameter(StatusState.normal.getValue(),NoticeMode.Invalidated.getValue());
        // 查询到今天为止所有未删除的通知
        String hql = " from Notice n where n.status= :p1 and n.noticeMode <> :p2";
        Date nowTime = Calendar.getInstance().getTime();
        List<Notice> noticeList = getEntityDao().find(hql, parameter);
        if (Collections3.isNotEmpty(noticeList)) {
            for (Notice n : noticeList) {
                if (NoticeMode.UnPublish.getValue().equals(n.getNoticeMode())
                        && n.getEffectTime() != null
                        && nowTime.compareTo(n.getEffectTime()) != -1) {//定时发布
                    this.publish(n);
                }else if (NoticeMode.Effective.getValue().equals(n.getNoticeMode())
                        && n.getEndTime() != null
                        && nowTime.compareTo(n.getEndTime()) != -1) {//到时失效
                    n.setNoticeMode(NoticeMode.Invalidated.getValue());
                    getEntityDao().update(n);
                }
                //取消置顶
                if (IsTop.Yes.getValue().equals(n.getIsTop())
                        && n.getEndTopDay() != null && n.getEndTopDay() >0) {
                    Date publishTime = (n.getPublishTime() == null) ? nowTime: n.getPublishTime();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(publishTime);
                    cal.add(Calendar.DATE, n.getEndTopDay());
                    if (nowTime.compareTo(cal.getTime()) != -1) {
                        n.setIsTop(IsTop.No.getValue());
                        getEntityDao().update(n);
                    }
                }
            }
        }
    }
}
