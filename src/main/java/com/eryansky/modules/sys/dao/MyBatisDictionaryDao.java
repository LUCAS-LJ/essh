/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.eryansky.modules.sys.dao;

import com.eryansky.common.orm.mybatis.MyBatisDao;
import com.eryansky.modules.sys.entity.Dictionary;

import java.util.List;

/**
 * MyBatis字典DAO接口
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @version 2014-7-16
 */
@MyBatisDao
public interface MyBatisDictionaryDao {
	
    Dictionary get(String id);
    
    List<Dictionary> find(Dictionary dictionary);
    
}
