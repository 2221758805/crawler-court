package com.pactera.crawler.common.bean;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Description: 代理服务器基本信息
 * Copyright (c) Pactera All Rights Reserved.
 * version 1.0 2016-5-20 上午9:56:51 by 张仁华（renhua.zhang@pactera.com）创建
 */
public class ProxyInfoBean {

    /**
     * 代理服务器
     */
    private String proxyHost;

    /**
     * 代理服务器端口
     */
    private Integer proxyPort;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 认证优先模式
     */
    private boolean authenticatePreemptively;

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public Integer getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAuthenticatePreemptively() {
        return authenticatePreemptively;
    }

    public void setAuthenticatePreemptively(boolean authenticatePreemptively) {
        this.authenticatePreemptively = authenticatePreemptively;
    }

    public void setProxyPort(Integer proxyPort) {
        this.proxyPort = proxyPort;
    }

    public ProxyInfoBean(String proxyHost, Integer proxyPort, String username, String password, boolean authenticatePreemptively) {
        super();
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.username = username;
        this.password = password;
        this.authenticatePreemptively = authenticatePreemptively;
    }

    public ProxyInfoBean() {
        super();
    }

    public ProxyInfoBean(String proxyHost, int proxyPort) {
        super();
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
