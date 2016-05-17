/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.eryansky.dao;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.eryansky.modules.sys.dao.MyBatisDictionaryDao;
import com.eryansky.modules.sys.entity.Dictionary;
import com.eryansky.modules.sys.service.DictionaryManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-07-12 22:16
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml",
        "classpath:applicationContext-mybatis.xml"})
public class MybatisTest {

    @Autowired
    private MyBatisDictionaryDao myBatisDictionaryDao;
    @Autowired
    private DictionaryManager dictionaryManager;

    @Test
    public void test() {
        Date d1 = new Date();
        Dictionary dictionary2 = dictionaryManager.getById(1L);
        Date d2 = new Date();
        System.out.println(d2.getTime() - d1.getTime());

        Dictionary dictionary = myBatisDictionaryDao.get("1");
        Date d3 = new Date();
        System.out.println(d3.getTime() - d2.getTime());

        System.out.println(JsonMapper.getInstance().toJson(dictionary));
        System.out.println(JsonMapper.getInstance().toJson(dictionary2));

//        List<Dictionary> dictionaryList = myBatisDictionaryDao.find(dictionary);
//        System.out.println(dictionaryList.size());
//        System.out.println(JsonMapper.getInstance().toJson(dictionaryList));
    }
}
