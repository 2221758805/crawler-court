package com.pactera.crawler.dishonesty.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageReader;

import com.pactera.crawler.dishonesty.bean.DishonestyFormBean;
import net.sourceforge.tess4j.TesseractException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import com.pactera.crawler.dishonesty.service.DishonestyCrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pactera.crawler.common.utils.CommonUtils;
import com.pactera.crawler.common.utils.HttpClientUtils;
import com.pactera.crawler.common.utils.ValidaCodeException;
import com.pactera.crawler.common.utils.ValidaCodeUtil;

/**
 * Description: 法院被执行人爬取
 * Copyright (c) Pactera All Rights Reserved.
 * version 1.0 2016-5-26 下午2:37:15 by 张仁华（renhua.zhang@pactera.com）创建
 */
public class DishonestyCrawlerServiceImpl implements DishonestyCrawlerService {

    private static final Logger LOG = LoggerFactory.getLogger(DishonestyCrawlerServiceImpl.class);

    private WebClient webClient;
    private HttpClientUtils httpClient;

    /**
     * 是否保存图片到本地
     */
    private boolean saveImage;

    /**
     * 验证码识别错误，重试次数(默认5次)
     */
    private int errorCount = 5;

    /**
     * 明细验证码识别错误，重试次数（默认5次）
     */
    private int detailErrorCount = 5;
    /**
     * 根地址
     */
    private String rootUrl;

    /**
     * 明细地址
     */
    private String detailUrl;

    public List<Map<String, String>> crawler(DishonestyFormBean formBean) {

        int index = 0;

		/*
         * 获取明细，重试控制
		 */
        do {
            try {
                return crawlerAllDetail(formBean);
            } catch (ValidaCodeException e) {
                // 如果是validaCode异常，则重试。
                LOG.error(e.getMessage(), e);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                break;
            }
        } while (++index < errorCount);

        LOG.error(CommonUtils.append("数据爬取失败，已重试:", index, "次"));
        return null;
    }

    private List<Map<String, String>> crawlerAllDetail(DishonestyFormBean formBean) throws IOException, ValidaCodeException {

        LOG.info("begin crawler " + rootUrl);
        // 请求法院被执行人首页
        HtmlPage courtPage = webClient.getPage(rootUrl);
        // 根据参数查询执行人信息，返回信息列表页面
        HtmlPage resultPage = queryAndReturnNewPage(formBean, courtPage);
        // 保存结果集
        List<Map<String, String>> result = new ArrayList<>();
        HtmlAnchor refreshCaptcha = courtPage.getAnchorByText("换一张");

        // 是否还有下一页
        boolean hashNext;
        int pageNo = 1; // 当前页

        do {

            hashNext = false;

			/*
             * 检查验证码是否正确
			 */
            HtmlTable resultTable = getResultTable(resultPage);
            if (resultTable == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(resultPage.asXml());
                }
                throw new ValidaCodeException("验证码识别错误。");
            }

            // 获取明细
            loopGetDetail(courtPage, resultTable, result, pageNo++);

			/*
             * 点击下一页
			 */
            for (final HtmlAnchor anchor : resultPage.getAnchors()) {
                if ("下一页".equals(anchor.asText())) {
                    hashNext = true;

                    // 刷新验证码
                    refreshCaptcha.click();
                    getImageAndParse(courtPage);

                    // 点击下一页
                    resultPage = anchor.click();

                    // 解析验证码
                    getImageAndParse(courtPage);
                    break;
                }
            }

        } while (hashNext);

        return result;
    }

    /**
     * Description: 获取验证码并且解析
     * Version1.0 2016-5-20 下午4:36:29 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param courtPage 主页面
     * @throws IOException
     */
    private void getImageAndParse(HtmlPage courtPage) throws IOException {
        String validaCode = recognizeText((HtmlImage) courtPage.getElementById("captchaImg"));
        LOG.info("validaCode:" + validaCode);

        DomElement validaCodeInput = courtPage.getElementById("pCode");
        if (validaCodeInput == null) {
            throw new NullPointerException("Content view failed ,validaCodeInput is null.");
        }
        validaCodeInput.setAttribute("value", validaCode);
    }

    /**
     * 获取返回结果table
     *
     * @param resultPage 结果页面
     * @return 结果table
     */
    private HtmlTable getResultTable(HtmlPage resultPage) {
        return (HtmlTable) resultPage.getElementById("Resultlist");
    }

    /**
     * 循环获取详细信息
     *
     * @param courtPage   主页面
     * @param resultTable 数据table
     * @param result      返回结果集
     * @param pageNo      页码
     * @throws IOException
     */
    private void loopGetDetail(HtmlPage courtPage, HtmlTable resultTable, List<Map<String, String>> result, int pageNo) throws IOException {

        // 获取表格里的所有a标签（明细链接）
        DomNodeList<HtmlElement> aList = resultTable.getElementsByTagName("a");
        HtmlAnchor refreshCaptcha = courtPage.getAnchorByText("换一张");
        int index = 0;

        for (HtmlElement htmlElement : aList) {
            if (htmlElement.getAttribute("class").equals("View")) {

                int ec = 0;
                do {

                    // 切换验证码
                    refreshCaptcha.click();

                    // 解析图片
                    String valideCode = recognizeText((HtmlImage) courtPage.getElementById("captchaImg"));
                    // 查询明细
                    String jsonData = queryDetail(htmlElement.getAttribute("id"), valideCode);
                    LOG.debug(jsonData);

                    // 验证返回jsondata正确性
                    if (jsonData != null && !jsonData.trim().equals("{}")) {
                        try {
                            result.add(new Gson().fromJson(jsonData, HashMap.class));
                            break;
                        } catch (JsonSyntaxException e) {
                            LOG.error(e.getMessage());
                        }
                    }

                    LOG.error("明细验证码识别错误。");
                    // 点击取消按钮
                    cancel(courtPage);

                    // 暂停一秒
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                    }
                } while (++ec < detailErrorCount);

                // 如果明细获取失败，则抛出异常
                if (ec >= detailErrorCount) {
                    LOG.error("爬取失败，已爬取明细：" + result);
                    throw new RuntimeException("明细数据获取失败，已重试：" + ec);
                }

                index++;
            }
        }

        if (LOG.isInfoEnabled()) {
            LOG.info(CommonUtils.append("current pageNo:", pageNo, ",obtain ", index, " pages."));
        }
    }

    /**
     * 点击取消按钮
     *
     * @param courtPage 主页面
     * @throws IOException
     */
    private void cancel(HtmlPage courtPage) throws IOException {
        DomNodeList<DomElement> buttons = courtPage.getElementsByTagName("button");
        for (DomElement domElement : buttons) {
            DomNodeList<HtmlElement> elementsByTagName = domElement.getElementsByTagName("span");
            if (elementsByTagName.size() == 1 && elementsByTagName.get(0).getTextContent().trim().equals("取消")) {
                domElement.click();
                break;
            }
        }
    }

    /**
     * Description: 查询明细， 为什么这里我会使用httpclient，呵呵。
     * 如果还用htmlunit，由于ajax页面用的是异步请求，
     * 所以当我页面点击后，数据还有一段时间才会返回过来，这时页面是获取不到数据的
     * ，也不知道ajax请求什么时候完成。并且ajax请求后还得修改页面表单性能肯定没有直接请求好的。
     * Version1.0 2016-5-20 下午4:13:13 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param id         数据id
     * @param validaCode 验证码
     * @return 查询数据，josn
     * @throws IOException
     */
    private String queryDetail(String id, String validaCode) throws IOException {

        StringBuilder cookieStr = new StringBuilder();
        for (com.gargoylesoftware.htmlunit.util.Cookie cookie : webClient.getCookieManager().getCookies()) {
            cookieStr.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
        }

        // 拼接请求url
        String url = CommonUtils.append(detailUrl, "?id=", id, "&pCode=", validaCode);
        HttpGet get = new HttpGet(url);
        get.addHeader("Cookie", cookieStr.substring(0, cookieStr.length() - 1));
        get.addHeader("Referer", rootUrl);
        get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.94 Safari/537.36");
        get.addHeader("X-Requested-With", "XMLHttpRequest");

		/*
         * 获取json数据
		 */
        try (CloseableHttpResponse response = httpClient.execute(get)) {

            if (LOG.isInfoEnabled()) {
                LOG.info(CommonUtils.append("get ", url, ", statusCode:", response.getStatusLine().getStatusCode()));
            }

            String jsonData = EntityUtils.toString(response.getEntity(), "UTF-8");
            get.releaseConnection();

            return jsonData;
        }
    }

    /**
     * Description: 查询数据，并返回新的数据页面
     * Version1.0 2016-5-20 下午2:25:17 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param formBean  from参数
     * @param courtPage 主页面
     * @return 结果页面
     * @throws IOException
     */
    private HtmlPage queryAndReturnNewPage(DishonestyFormBean formBean, HtmlPage courtPage) throws IOException {
        /*
         * 解析form表单，获取需求set的input控件
		 */
        HtmlForm form = (HtmlForm) courtPage.getElementById("searchForm");
        HtmlInput pName = form.getInputByName("pName"); // 被执行人姓名/名称
        HtmlInput cardnum = form.getInputByName("pCardNum"); // 身份证号码/组织机构代码
        HtmlInput captcha = form.getInputByName("pCode"); // 验证码
        HtmlElement submitBut = form.getElementsByAttribute("div", "class", "login_button").get(0); // 查询按钮

		/*
         * 获取验证码图片
		 */
        HtmlImage validaCodeImg = (HtmlImage) courtPage.getElementById("captchaImg");
        if (validaCodeImg == null) {
            throw new NullPointerException("validaCodeImg is null.");
        }
        String validaCode = recognizeText(validaCodeImg);

		/*
         * 填充form表单
		 * 
		 */
        pName.setValueAttribute(formBean.getPname().trim());
        cardnum.setValueAttribute(formBean.getCardNum().trim());
        captcha.setValueAttribute(validaCode);

        // 提交表单，返回提交表单后跳转的页面
        return submitBut.click();
    }

    /**
     * Description: 识别文字
     * Version1.0 2016-5-20 下午4:09:15 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param validaCodeImg 验证码tag对象
     * @return 识别文本
     * @throws IOException
     */
    private String recognizeText(HtmlImage validaCodeImg) throws IOException {
        try {
            BufferedImage bi = validaCodeImgToJpg(validaCodeImg);
            String validaCode = ValidaCodeUtil.recognizeText(bi, 6).replaceAll("\n|\r", "").trim().replace(" ", "");

            // 保存图片
            if (saveImage) {
                ValidaCodeUtil.saveImage(CommonUtils.append("download-images/", validaCode, ".jpg"), bi);
            }

            return validaCode;
        } catch (TesseractException e) {
            LOG.error(e.getMessage());
            throw new RuntimeException("验证码识别失败。");
        }
    }

    /**
     * Description: 将image对象转换成jpg图片字节数组
     * Version1.0 2016-5-20 下午2:00:13 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param validaCodeImg 验证码tag对象
     * @return bufferedImage
     * @throws IOException
     */
    private BufferedImage validaCodeImgToJpg(HtmlImage validaCodeImg) throws IOException {

        ImageReader imageReader = validaCodeImg.getImageReader();
        if (imageReader == null) {
            throw new NullPointerException("imageReader is null.");
        }

        return imageReader.read(0);
    }

    public WebClient getWebClient() {
        return webClient;
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public HttpClientUtils getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClientUtils httpClient) {
        this.httpClient = httpClient;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public boolean isSaveImage() {
        return saveImage;
    }

    public void setSaveImage(boolean saveImage) {
        this.saveImage = saveImage;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getDetailErrorCount() {
        return detailErrorCount;
    }

    public void setDetailErrorCount(int detailErrorCount) {
        this.detailErrorCount = detailErrorCount;
    }

}
