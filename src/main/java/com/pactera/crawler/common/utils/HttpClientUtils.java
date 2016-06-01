package com.pactera.crawler.common.utils;

import com.pactera.crawler.common.bean.ProxyInfoBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public class HttpClientUtils {

    private CloseableHttpClient httpClient;
    private ProxyInfoBean proxyInfo;
    private HttpHost target;
    private HttpClientContext localContext;

    /**
     * Description: 初始化httpClient
     * Version1.0 2016-5-23 上午11:00:31 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param proxyInfo 代理服务器信息
     */
    public void init(ProxyInfoBean proxyInfo) {

        if (proxyInfo == null || StringUtils.isBlank(proxyInfo.getProxyHost())) {
            httpClient = HttpClients.createDefault();
        } else {
            this.target = new HttpHost(proxyInfo.getProxyHost(), proxyInfo.getProxyPort());
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(target.getHostName(), target.getPort()), new UsernamePasswordCredentials(proxyInfo.getUsername(), proxyInfo.getPassword()));
            httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

            this.proxyInfo = proxyInfo;

            /*
             * 抢先认证
             */
            if (proxyInfo.isAuthenticatePreemptively()) {
                // Create AuthCache instance
                AuthCache authCache = new BasicAuthCache();
                DigestScheme digestAuth = new DigestScheme();
                authCache.put(target, digestAuth);

                // Add AuthCache to the execution context
                this.localContext = HttpClientContext.create();
                localContext.setAuthCache(authCache);
            }
        }
    }

    /**
     * Description: 执行请求
     * Version1.0 2016-5-23 上午11:03:10 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param request httpRequest
     * @return httpResponse
     * @throws IOException
     */
    public CloseableHttpResponse execute(HttpRequestBase request) throws IOException {
        if (proxyInfo != null && proxyInfo.isAuthenticatePreemptively()) {
            return httpClient.execute(target, request, localContext);

        } else {
            return httpClient.execute(request);
        }

    }

    public CloseableHttpClient getHttpClient() {
        return httpClient;
    }

}
