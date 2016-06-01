package com.pactera.crawler;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.google.gson.Gson;
import com.pactera.crawler.beexecuted.bean.BeExecutedFormBean;
import com.pactera.crawler.beexecuted.service.impl.BeExecutedCrawlerServiceImpl;
import com.pactera.crawler.common.bean.ProxyInfoBean;
import com.pactera.crawler.common.factory.WebClientCrawlerFactory;
import com.pactera.crawler.common.utils.HttpClientUtils;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class BeExecutedCrawlerTest {

    @Test
    public void test() {

		/*
         * 代理服务器信息
		 */
        ProxyInfoBean proxyInfo = new ProxyInfoBean();
//        proxyInfo.setProxyHost("11.1.0.21");
//        proxyInfo.setProxyPort(80);
//        proxyInfo.setUsername("kfcs");
//        proxyInfo.setPassword("95594Bos");
//        proxyInfo.setAuthenticatePreemptively(true);

		/*
         * 创建模拟浏览器
		 */
        WebClient webClient = WebClientCrawlerFactory.getWebClient(proxyInfo, BrowserVersion.CHROME);
        // webClient.setHTMLParserListener(HTMLParserListener.LOG_REPORTER);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setCssEnabled(false);

        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);

		/*
         * 创建httpClient
		 */
        HttpClientUtils httpClient = new HttpClientUtils();
        httpClient.init(proxyInfo);

        for (int i = 0; i < 1; i++) {

			/*
             * 法院爬取service
			 */
            BeExecutedCrawlerServiceImpl crawlerService = new BeExecutedCrawlerServiceImpl();
            crawlerService.setWebClient(webClient);
            crawlerService.setHttpClient(httpClient);
            crawlerService.setSaveImage(true);
            crawlerService.setErrorCount(5);
            crawlerService.setDetailErrorCount(5);
            crawlerService.setRootUrl("http://zhixing.court.gov.cn/search");
            crawlerService.setDetailUrl("http://zhixing.court.gov.cn/search/newdetail");

            // 爬取数据
            List<Map<String, String>> result = crawlerService.crawler(new BeExecutedFormBean("重庆金禾房地产开发有限公司", "20299468-0"));
            System.out.println(result.size());
            System.out.println(new Gson().toJson(result));
        }
    }
}
