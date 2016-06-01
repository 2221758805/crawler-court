package com.pactera.crawler.common.factory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultCredentialsProvider;
import com.gargoylesoftware.htmlunit.WebClient;
import com.pactera.crawler.common.bean.ProxyInfoBean;

/**
 * Description: 流浪器模拟器对象工厂
 * Copyright (c) Pactera All Rights Reserved.
 * version 1.0 2016-5-20 上午10:14:52 by 张仁华（renhua.zhang@pactera.com）创建
 */
public class WebClientCrawlerFactory {

    private static final Logger LOG = LoggerFactory.getLogger(WebClientCrawlerFactory.class);

    /**
     * Description: 获取webClient
     * Version1.0 2016-5-20 上午10:09:54 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param proxyInfo      代理信息，可以为null
     * @param browserVersion 浏览器版本，为null则使用默认浏览器
     * @return webClient
     */
    public static WebClient getWebClient(ProxyInfoBean proxyInfo, BrowserVersion browserVersion) {

        if (LOG.isDebugEnabled()) {
            LOG.debug(proxyInfo.toString());
        }

        // 给予默认值
        if (browserVersion == null) {
            browserVersion = BrowserVersion.getDefault();
        }

        if (proxyInfo == null || StringUtils.isBlank(proxyInfo.getProxyHost()) || proxyInfo.getProxyPort() == null) {
            return new WebClient(browserVersion);
        }

        // 浏览器模拟器，并使用代理服务器
        WebClient webClient = new WebClient(browserVersion, proxyInfo.getProxyHost(), proxyInfo.getProxyPort());

        // 设置代理用户名和密码
        if (StringUtils.isNotBlank(proxyInfo.getUsername())) {
            DefaultCredentialsProvider credentialsProvider = (DefaultCredentialsProvider) webClient.getCredentialsProvider();
            credentialsProvider.addCredentials(proxyInfo.getUsername(), proxyInfo.getPassword());
        }
        return webClient;
    }

    /**
     * Description: 获取webClient
     * Version1.0 2016-5-20 上午10:09:54 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param proxyInfo 代理信息，可以为null
     * @return webClient
     */
    public static WebClient getWebClient(ProxyInfoBean proxyInfo) {
        return getWebClient(proxyInfo, null);
    }

    /**
     * Description: 获取webClient
     * Version1.0 2016-5-20 上午10:09:54 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param browserVersion 浏览器版本，为null则使用默认浏览器
     * @return webClient
     */
    public static WebClient getWebClient(BrowserVersion browserVersion) {
        return getWebClient(null, browserVersion);
    }

    /**
     * Description: 获取webClient
     * Version1.0 2016-5-20 上午10:09:54 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @return webClient
     */
    public static WebClient getWebClient() {
        return getWebClient(null, null);
    }

}

/*
 * webClient常见参数设置： //设置webClient的相关参数
 * webClient.getOptions().setJavaScriptEnabled(true);
 * webClient.getOptions().setActiveXNative(false);
 * webClient.getOptions().setCssEnabled(false);
 * webClient.getOptions().setThrowExceptionOnScriptError(false);
 * webClient.waitForBackgroundJavaScript(600*1000);
 * webClient.setAjaxController(new NicelyResynchronizingAjaxController());
 * 
 * webClient.getOptions().setJavaScriptEnabled(true);
 * 
 * webClient.setJavaScriptTimeout(3600*1000);
 * webClient.getOptions().setRedirectEnabled(true);
 * webClient.getOptions().setThrowExceptionOnScriptError(true);
 * webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
 * webClient.getOptions().setTimeout(3600*1000);
 * webClient.waitForBackgroundJavaScript(600*1000);
 * 
 * //webClient.waitForBackgroundJavaScript(600*1000);
 * webClient.setAjaxController(new NicelyResynchronizingAjaxController());
 */