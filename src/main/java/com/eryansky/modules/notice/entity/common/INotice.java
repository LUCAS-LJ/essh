package com.eryansky.modules.notice.entity.common;

import java.util.Date;

/**
 * Copyright (c) 2012-2014 http://www.eryansky.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
public interface INotice {

    String getTitle();
    String getType();
    String getTypeView();
    String getContent();
    String getPublishUserName();
    String getPublishOrganName();
    Date getPublishTime();
    Integer getIsTop();
    String getIsTopView();
    String getNoticeModeView();
}
