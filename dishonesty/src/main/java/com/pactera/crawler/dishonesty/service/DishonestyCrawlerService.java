package com.pactera.crawler.dishonesty.service;

import com.pactera.crawler.dishonesty.bean.DishonestyFormBean;

import java.util.List;
import java.util.Map;

/**
 * Description: 法院失信被执行人
 * Copyright (c) Pactera All Rights Reserved.
 * version 1.0 2016-5-26 下午2:37:41 by 张仁华（renhua.zhang@pactera.com）创建
 */
public interface DishonestyCrawlerService {

    /**
     * Description: 法院失信执行人信息查询
     * Version1.0 2016-5-24 上午10:26:25 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param formBean 表单参数
     * @return 失信被执行人记录
     */
    public List<Map<String, String>> crawler(DishonestyFormBean formBean);
}
