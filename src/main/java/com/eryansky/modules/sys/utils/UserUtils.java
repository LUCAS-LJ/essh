/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.utils;

import com.eryansky.common.spring.SpringContextHolder;
import com.eryansky.common.utils.ConvertUtils;
import com.eryansky.common.utils.collections.Collections3;
import com.eryansky.modules.sys.entity.User;
import com.eryansky.modules.sys.service.UserManager;

import java.util.List;

/**
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2014-11-25
 */
public class UserUtils {

    private static UserManager userManager = SpringContextHolder.getBean(UserManager.class);

    /**
     * 根据userId查找用户姓名
     * @param userId 用户ID
     * @return
     */
    public static String getUserName(Long userId){
        if(userId != null){
            User user = userManager.loadById(userId);
            if(user != null){
                return user.getName();
            }
        }
        return null;
    }

    /**
     * 根据userId查找用户姓名
     * @param userIds 用户ID集合
     * @return
     */
    public static String getUserNames(List<Long> userIds){
        if(Collections3.isNotEmpty(userIds)){
            List<User> list = userManager.findUsersByIds(userIds);
            return ConvertUtils.convertElementPropertyToString(list, "name", ", ");
        }
        return null;
    }
}
