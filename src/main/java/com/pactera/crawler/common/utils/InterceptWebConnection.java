package com.pactera.crawler.common.utils;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.FalsifyingWebConnection;

/**
 * webClient请求资源监控（自定义过滤/拦截）
 */
public class InterceptWebConnection extends FalsifyingWebConnection {
    public InterceptWebConnection(WebClient webClient) throws IllegalArgumentException {
        super(webClient);
    }

    @Override
    public WebResponse getResponse(WebRequest request) throws IOException {

        System.out.println(request.getUrl().getPath());


        WebResponse response = super.getResponse(request);
//		System.out.println(response.getWebRequest().getUrl().toString());
//		if (response.getWebRequest().getUrl().toString().endsWith("dom-drag.js")) {
//			return createWebResponse(response.getWebRequest(), "", "application/javascript", 200, "Ok");
//		}
        return super.getResponse(request);
    }


}