/**
 *  Copyright (c) 2013-2014 http://www.jfit.com.cn
 *
 *          江西省锦峰软件科技有限公司         
 */
package com.eryansky.modules.notice.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.notice.entity.Notice;
import com.eryansky.modules.notice.service.NoticeManager;
import com.eryansky.modules.notice.service.NoticeScopeManager;
import com.eryansky.modules.sys.service.UserManager;

/**
 * @author : 温春平 wencp@jx.tobacco.gov.cn
 * @date : 2014-08-01 17:44
 */
public class NoticeUtils {

    public static final String DIC_NOTICE = "notice";
    public static final String ROLE_SYSTEM_MANAGER = "system_manager";
    public static final String ROLE_NOTICE_MANAGER = "notice_manager";

    public static final String MSG_REPEAT = "转发：";

    private NoticeUtils(){

    }

    private static NoticeManager noticeManager = SpringContextHolder.getBean(NoticeManager.class);
    private static NoticeScopeManager noticeScopeManager = SpringContextHolder.getBean(NoticeScopeManager.class);
    private static UserManager userManager = SpringContextHolder.getBean(UserManager.class);


    /**
     * 根据ID查找
     * @param noticeId
     * @return
     */
    public static Notice getNotice(Long noticeId) {
        return noticeManager.loadById(noticeId);
    }

    /**
     * 判断当前登录用户是否读取通知
     * @param noticeId 通知ID
     * @return
     */
    public static boolean isRead(Long noticeId) {
        return noticeScopeManager.isRead(SecurityUtils.getCurrentSessionInfo().getUserId(), noticeId);
    }

    /**
     * 通知管理员 超级管理 + 系统管理员 + 通知管理员
     * @param userId 用户ID 如果为null,则为当前登录用户ID
     * @return
     */
    public static boolean isNoticeAdmin(Long userId){
        Long _userId = userId;
        if(_userId == null){
            SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
            _userId = sessionInfo.getUserId();
        }

        boolean isAdmin = false;
        if (userManager.isSuperUser(_userId) || SecurityUtils.isPermittedRole(ROLE_SYSTEM_MANAGER)
                || SecurityUtils.isPermittedRole(ROLE_NOTICE_MANAGER)) {//系统管理员 + 通知管理员
            isAdmin = true;
        }
        return isAdmin;
    }
}
