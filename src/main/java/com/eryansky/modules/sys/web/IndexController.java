/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.web;

import com.eryansky.common.model.Menu;
import com.eryansky.common.model.TreeNode;
import com.eryansky.common.orm.Page;
import com.eryansky.common.orm.entity.StatusState;
import com.eryansky.common.orm.hibernate.DefaultEntityManager;
import com.eryansky.common.utils.StringUtils;
import com.eryansky.common.web.springmvc.SimpleController;
import com.eryansky.common.web.springmvc.SpringMVCHolder;
import com.eryansky.common.web.utils.WebUtils;
import com.eryansky.core.security.SecurityUtils;
import com.eryansky.core.security.SessionInfo;
import com.eryansky.core.security.annotation.RequiresUser;
import com.eryansky.modules.sys._enum.ResourceType;
import com.eryansky.modules.sys.entity.Resource;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.ResourceManager;
import com.eryansky.modules.sys.service.UserManager;
import com.eryansky.utils.AppConstants;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.List;

/**
 * 主页管理
 *
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date   2014-09-16 10:30
 */
@Controller
@RequestMapping(value = "${adminPath}")
public class IndexController extends SimpleController {

    @Autowired
    private UserManager userManager;
    @Autowired
    private ResourceManager resourceManager;
    @Autowired
    private DefaultEntityManager defaultEntityManager;


    /**
     * 欢迎页面 登录页
     * @return
     * @throws Exception
     */
    @RequiresUser(required = false)
    @RequestMapping(value = {"index/welcome", ""})
    public String welcome() throws Exception {
        return "login";
    }

    @RequestMapping(value = {"index"})
    public String index(String theme) {
        //根据客户端指定的参数跳转至 不同的主题 如果未指定 默认:index
        if (StringUtils.isNotBlank(theme) && (theme.equals("app") || theme.equals("index"))) {
            return "layout/" + theme;
        } else {
            return "layout/index";
        }
    }

    @RequestMapping("index/west")
    public ModelAndView west() {
        ModelAndView modelAnView = new ModelAndView("layout/west");
        User sessionUser = SecurityUtils.getCurrentUser();
        modelAnView.addObject("user", sessionUser);
        String userPhoto = null;
        if(StringUtils.isNotBlank(sessionUser.getPhoto())){
            userPhoto = SpringMVCHolder.getRequest().getContextPath()+ sessionUser.getPhoto();
        }else{
            userPhoto = SpringMVCHolder.getRequest().getContextPath()+"/static/img/icon_boy.png";
        }
        modelAnView.addObject("userPhoto", userPhoto);
        return modelAnView;
    }



    /**
     * 导航菜单.
     */
    @ResponseBody
    @RequestMapping(value = {"index/navTree"})
    public List<TreeNode> navTree(HttpServletResponse response) {
        WebUtils.setNoCacheHeader(response);
        List<TreeNode> treeNodes = Lists.newArrayList();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            treeNodes = resourceManager.getNavMenuTreeByUserId(sessionInfo.getUserId());
        }
        return treeNodes;
    }


    /**
     * 桌面版 开始菜单
     */
    @RequestMapping(value = {"index/startMenu"})
    @ResponseBody
    public List<Menu> startMenu() {
        List<Menu> menus = Lists.newArrayList();
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            List<Resource> rootResources = Lists.newArrayList();
            User superUser = userManager.getSuperUser();
            if (sessionInfo != null && superUser != null
                    && sessionInfo.getUserId().equals(superUser.getId())) {// 超级用户
                rootResources = resourceManager.getByParentId(null, StatusState.normal.getValue());
            } else if (sessionInfo != null) {
                rootResources = resourceManager.getResourcesByUserId(sessionInfo.getUserId(), null);
                //去除非菜单资源
                Iterator<Resource> iterator = rootResources.iterator();
                while (iterator.hasNext()) {
                    if (!ResourceType.menu.getValue().equals(iterator.next().getType())) {
                        iterator.remove();
                    }
                }
            }
            for (Resource parentResource : rootResources) {
                Menu menu = this.resourceToMenu(parentResource, true);
                if (menu != null) {
                    menus.add(menu);
                }
            }
        }
        return menus;
    }


    /**
     * 桌面版 桌面应用程序列表
     */
    @RequestMapping(value = {"index/apps"})
    @ResponseBody
    public List<Menu> apps() {
        HttpServletRequest request = SpringMVCHolder.getRequest();
        List<Menu> menus = Lists.newArrayList();
        String head = this.getHeadFromUrl(request.getRequestURL().toString());
        SessionInfo sessionInfo = SecurityUtils.getCurrentSessionInfo();
        if (sessionInfo != null) {
            List<Resource> resources = Lists.newArrayList();
            User superUser = userManager.getSuperUser();
            if (sessionInfo != null && superUser != null
                    && sessionInfo.getUserId().equals(superUser.getId())) {// 超级用户
                resources = resourceManager.getAll("orderNo", Page.ASC);
            } else if (sessionInfo != null) {
                resources = resourceManager.getResourcesByUserId(sessionInfo.getUserId());
            }
            for (Resource resource : resources) {
                if (resource != null && StringUtils.isNotBlank(resource.getUrl())) {
                    if (ResourceType.menu.getValue().equals(resource.getType())) {
                        Menu menu = new Menu();
                        menu.setId(resource.getId().toString());
                        menu.setText(resource.getName());
                        String url = resource.getUrl();
                        if (url.startsWith("http")) {
                            url = resource.getUrl();
                        } else if (url.startsWith("/")) {
                            url = head + request.getContextPath()  + url;
                        } else {
                            url = head + request.getContextPath() + AppConstants.getAdminPath()+ "/" + url;
                        }
                        menu.setHref(url);
                        menu.setIconCls(resource.getIconCls());
                        menus.add(menu);
                    }
                }

            }
        }
        return menus;
    }

    /**
     * 资源转M
     *
     * @param resource  资源
     * @param isCascade 是否级联
     * @return
     */
    private Menu resourceToMenu(Resource resource, boolean isCascade) {
        HttpServletRequest request = SpringMVCHolder.getRequest();
        Assert.notNull(resource, "参数resource不能为空");
        String head = this.getHeadFromUrl(request.getRequestURL().toString());
        if (ResourceType.menu.getValue().equals(resource.getType())) {
            Menu menu = new Menu();
            menu.setId(resource.getId().toString());
            menu.setText(resource.getName());
            String url = resource.getUrl();
            if (url.startsWith("http")) {
                url =  resource.getUrl();
            } else if (url.startsWith("/")) {
                url = head + request.getContextPath()  + url;
            } else {
                url = head + request.getContextPath() + AppConstants.getAdminPath()+ "/" + url;
            }
            menu.setHref(url);
            if (isCascade) {
                List<Menu> childrenMenus = Lists.newArrayList();
                for (Resource subResource : resource.getSubResources()) {
                    if (ResourceType.menu.getValue().equals(subResource.getType())) {
                        childrenMenus.add(resourceToMenu(subResource, true));
                    }
                }
                menu.setChildren(childrenMenus);
            }
            return menu;
        }
        return null;
    }

    /**
     * 根据URL地址获取请求地址前面部分信息
     *
     * @param url
     * @return
     */
    private String getHeadFromUrl(String url) {
        int firSplit = url.indexOf("//");
        String proto = url.substring(0, firSplit + 2);
        int webSplit = url.indexOf("/", firSplit + 2);
        int portIndex = url.indexOf(":", firSplit);
        String webUrl = url.substring(firSplit + 2, webSplit);
        String port = "";
        if (portIndex >= 0) {
            webUrl = webUrl.substring(0, webUrl.indexOf(":"));
            port = url.substring(portIndex + 1, webSplit);
        } else {
            port = "80";
        }
        return proto + webUrl + ":" + port;
    }


}
