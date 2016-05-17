/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.sys.service;

import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.EntityManager;
import com.eryansky.common.orm.hibernate.HibernateDao;
import com.eryansky.common.orm.hibernate.Parameter;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys.entity.Post;
import com.eryansky.modules.sys.entity.User;
import org.apache.commons.lang3.Validate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 岗位管理 Service
 * @author : 温春平 wencp@jx.tobacco.gov.cn
 * @date : 2014-06-09 14:07
 */
@Service
public class PostManager extends
        EntityManager<Post, Long> {

    private HibernateDao<Post, Long> postDao;

    @Autowired
    private UserManager userManager;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        postDao = new HibernateDao<Post, Long>(
                sessionFactory, Post.class);
    }

    @Override
    protected HibernateDao<Post, Long> getEntityDao() {
        return postDao;
    }

    /**
     * 根据机构ID以及岗位名称查找
     * @param organId 机构ID
     * @param postName 岗位名称
     */
    public Post getPostByON(Long organId,String postName){
        Validate.notNull(organId, "参数[organId]不能为null");
        Validate.notNull(postName, "参数[postName]不能为null或空");
        Parameter parameter = new Parameter(organId,postName);
        List<Post> list = getEntityDao().find("from Post p where p.organ.id = :p1 and p.name = :p2",parameter);
        return list.isEmpty() ? null:list.get(0);
    }


    /**
     * 根据机构ID以及岗位编码查找
     * @param organId 机构ID
     * @param postCode 岗位编码
     */
    public Post getPostByOC(Long organId,String postCode){
        Validate.notNull(organId, "参数[organId]不能为null");
        Validate.notNull(postCode, "参数[postCode]不能为null或空");
        Parameter parameter = new Parameter(organId,postCode);
        List<Post> list = getEntityDao().find("from Post p where p.organ.id = :p1 and p.code = :p2",parameter);
        return list.isEmpty() ? null:list.get(0);
    }


    /**
     * 得到岗位所在部门的所有用户
     * @param postId 岗位ID
     * @return
     */
    public List<User> getPostOrganUsersByPostId(Long postId){
        Validate.notNull(postId, "参数[postId]不能为null");
        Post post = super.loadById(postId);
        if(post == null){
            return null;
        }
        return post.getOrgan().getUsers();
    }


    /**
     * 用户可选岗位列表
     * 如果organId不为null，则忽略参数userId；
     * 如果userId、organId都为空，则返回所有.
     * @param userId 用户ID
     * @param organId 机构ID
     * @return
     */
    public List<Post> getSelectablePosts(Long userId,Long organId) {
        if(userId ==null && organId == null){
            logger.warn("参数[userId，organId]至少有一个不为null.");
        }

        List<Post> list = null;
        StringBuffer hql = new StringBuffer();
        Parameter parameter = new Parameter();
        hql.append("from Post p where p.status = :status ");
        parameter.put("status",StatusState.normal.getValue());
        if(organId != null){
            hql.append(" and  p.organ.id = :organId");
            parameter.put("organId",organId);
        }else{
            if (userId != null) {
                User user = userManager.loadById(userId);
                List<Long> userOrganIds = user.getOrganIds();
                if(Collections3.isNotEmpty(userOrganIds)){
                    hql.append(" and  p.organ.id in (:userOrganIds)");
                    parameter.put("userOrganIds",userOrganIds);
                }else{
                    logger.warn("用户[{}]未设置部门.",new Object[]{user.getLoginName()});
                }

            }

        }
        list = getEntityDao().find(hql.toString(),parameter);
        return list;
    }
}