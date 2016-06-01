package com.pactera.crawler.dishonesty.bean;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Description: 法院被执行人form表单参数
 * Copyright (c) Pactera All Rights Reserved.
 * version 1.0 2016-5-20 下午2:19:10 by 张仁华（renhua.zhang@pactera.com）创建
 */
public class DishonestyFormBean {

    /**
     * 被执行人姓名/名称
     */
    private String pname;

    /**
     * 身份证号码/组织机构代码
     */
    private String cardNum;

    /**
     * 省份
     */
    private String province;

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getCardNum() {
        return cardNum;
    }

    public void setCardNum(String cardNum) {
        this.cardNum = cardNum;
    }

    public DishonestyFormBean(String pname, String cardNum, String province) {
        super();
        this.pname = pname;
        this.cardNum = cardNum;
        this.province = province;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public DishonestyFormBean() {
        super();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
