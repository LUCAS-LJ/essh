/**
 *  Copyright (c) 2012-2014 http://www.eryansky.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 */
package com.eryansky.core.security.annotation;

import com.eryansky.core.security._enum.Logical;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.eryansky.core.security._enum.Logical.AND;

/**
 * 需要的权限
 * @author : 尔演&Eryan eryanwcp@gmail.com
 * @date : 2014-06-11 20:06
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresPermissions {

    String[] value();

    Logical logical() default AND;

}