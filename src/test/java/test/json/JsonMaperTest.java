/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package test.json;

import com.eryansky.common.utils.mapper.JsonMapper;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-04-29 19:37
 */
public class JsonMaperTest {
    public static void main(String[] args) {
        new JsonMaperTest().time();
    }

    public void test(){
        JsonMapper jsonMapper = JsonMapper.getInstance();
        List<Javabean> list = Lists.newArrayList();
        Javabean javabean = new Javabean("name1",100,new Date());
        list.add(javabean);
//        传统模式 转换所有属性  需要在bean上加上注解 @JsonFilter(" ")
        System.out.println(jsonMapper.toJson(javabean));
//        排除属性
        System.out.println(jsonMapper.toJsonWithExcludeProperties(javabean,new String[]{"name"}));
        //转换指定属性
        Javabean javabean2 = new Javabean("name2",101,new Date());
        list.add(javabean2);
        System.out.println(jsonMapper.toJson(javabean));

        //集合属性过滤
        System.out.println(jsonMapper.toJson(list, Javabean.class,new String[]{"name"}));
    }

    public void time(){
        JsonMapper jsonMapper = JsonMapper.getInstance();
        List<Javabean> list = Lists.newArrayList();
        for(int i=0;i<10000;i++){
            Javabean javabean = new Javabean("name1",100,new Date());
            list.add(javabean);
        }

        Date d1 = new Date();
        System.out.println(jsonMapper.toJson(list));
        Date d2 = new Date();
        System.out.println(d2.getTime() - d1.getTime());
    }
}
