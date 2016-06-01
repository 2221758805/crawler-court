package com.pactera.crawler.common.utils;

import net.sourceforge.tess4j.TessAPI1;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Description: 验证码识别工具
 * Copyright (c) Pactera All Rights Reserved.
 * version 1.0 2016-5-20 上午10:48:07 by 张仁华（renhua.zhang@pactera.com）创建
 */
public class ValidaCodeUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ValidaCodeUtil.class);

    /**
     * Description: 获取图片文本
     * Version1.0 2016-5-20 下午2:06:33 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param bi 图片buffered
     * @return 识别字符串
     * @throws TesseractException
     */
    public static String recognizeText(BufferedImage bi) throws TesseractException {
        return recognizeText(bi, TessAPI1.TessPageSegMode.PSM_AUTO);
    }

    /**
     * Description: 获取图片文本
     * Version1.0 2016-5-20 下午2:06:33 by 张仁华（renhua.zhang@pactera.com）创建
     *
     * @param bi      图片buffered
     * @param segMode 识别模式
     * @return 识别字符串
     * @throws TesseractException
     */
    public static String recognizeText(BufferedImage bi, int segMode) throws TesseractException {

        if (bi == null) {
            LOG.debug("BufferedImage is null.");
            return "";
        }

        Tesseract1 tesseract1 = new Tesseract1();
        tesseract1.setPageSegMode(segMode);
        return tesseract1.doOCR(bi);
    }

    /**
     * 保存图片
     *
     * @param path 存储目录
     * @param bi   图片buffered
     * @throws IOException
     */
    public static void saveImage(String path, BufferedImage bi) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        try (FileOutputStream bs = new FileOutputStream(file); ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);) {
            ImageIO.write(bi, "jpg", imOut);
        }
    }
}
