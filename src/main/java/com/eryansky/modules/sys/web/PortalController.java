/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Result;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.modules.notice.entity.NoticeScope;
import com.eryansky.modules.notice.service.NoticeScopeManager;
import com.eryansky.modules.sys.service.BugManager;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Portal主页门户管理
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2014-07-31 12:30
 */
@Controller
@RequestMapping(value = "${adminPath}/portal")
public class PortalController extends SimpleController {

    @Autowired
    private NoticeScopeManager noticeScopeManager;

    @RequestMapping("")
    public ModelAndView portal() {
        ModelAndView modelAnView = new ModelAndView("layout/portal");
        return modelAnView;
    }


    /**
     * 我的通知
     *
     * @return
     */
    @RequestMapping("notice")
    public ModelAndView notice() {
        ModelAndView modelAnView = new ModelAndView("layout/portal-notice");
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            List<NoticeScope> noticeScopes = noticeScopeManager.getUserNewNotices(sessionInfo.getUserId(), null);
            modelAnView.addObject("noticeScopes", noticeScopes);
        }

        return modelAnView;
    }

    /**
     * 个人消息中心
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("mymessages")
    @ResponseBody
    public Result mymessages(HttpServletResponse response) throws Exception {
        WebUtils.setNoCacheHeader(response);
        Result result = null;
        Map<String, Long> map = Maps.newHashMap();
        // 当前登录用户
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        long noticeScopes = noticeScopeManager.getUserUnreadNoticeNum(sessionInfo.getUserId());
        map.put("noticeScopes", noticeScopes);

        result = Result.successResult().setObj(map);
        return result;
    }



}
