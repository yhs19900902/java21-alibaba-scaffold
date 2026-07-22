package com.yhs.base.captcha;

import com.yhs.base.pojo.vo.CaptchaInfo;
import com.yhs.base.utils.LogUtil;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @author 03952-yehuasheng
 * @version Id: CaptchaBuilder.java, v0.1 2023/9/12 11:04 yehuasheng Exp $
 */
public class CaptchaBuilder {
    /**
     * 日志
     */
    public static final Logger logger = LogUtil.getLogger(CaptchaBuilder.class.getName());
    /**
     * 验证码范围,去掉0(数字)和O(拼音)容易混淆的(小写的1和L也可以去掉,大写不用了)
     */
    private final char[] codeSequence = {'a', 'b', 'c', 'd', 'e', 'f', 'h', 'k', 'm', 'n', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'H', 'K', 'L', 'M', 'N', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};
    /**
     * 随机数
     */
    private final Random random = new Random();
    /**
     * 图片的宽度
     */
    private int width = 160;
    /**
     * 图片的高度
     */
    private int height = 40;
    /**
     * 验证码字符个数
     */
    private int codeCount = 5;
    /**
     * 验证码干扰线数
     */
    private int lineCount = 59;

    public CaptchaBuilder() {
    }

    public CaptchaBuilder(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public CaptchaBuilder(int width, int height, int codeCount, int lineCount) {
        this.width = width;
        this.height = height;
        this.codeCount = codeCount;
        this.lineCount = lineCount;
    }

    public CaptchaInfo createBaseCaptcha() {
        int x;
        int fontHeight;
        int codeY;
        int red;
        int green;
        int blue;

        x = width / (codeCount + 5);//每个字符的宽度
        fontHeight = height - 16;//字体的高度
        codeY = height - 8;

        // 图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        // 将图像填充为白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 创建字体
        Font font = new Font("Times New Roman", Font.PLAIN, fontHeight);
        g.setFont(font);

        for (int i = 0; i < lineCount; i++) {
            // 设置随机开始和结束坐标
            int xs = random.nextInt(width);//x坐标开始
            int ys = random.nextInt(height);//y坐标开始
            int xe = xs + random.nextInt(width / 8);//x坐标结束
            int ye = ys + random.nextInt(height / 8);//y坐标结束

            // 产生随机的颜色值
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            g.setColor(new Color(red, green, blue));
            g.drawLine(xs, ys, xe, ye);
        }

        // randomCode记录随机产生的验证码
        StringBuilder randomCode = new StringBuilder();
        // 随机产生codeCount个字符的验证码。
        for (int i = 0; i < codeCount; i++) {
            String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
            // 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            g.setColor(new Color(red, green, blue));

            g.drawString(strRand, 1 + (i * 2 * x), codeY);
            // 将产生的四个随机数组合在一起。
            randomCode.append(strRand);
        }
        String codes = randomCode.toString();
        String imgBase64Code = getImageBinary(buffImg);
        if (StringUtils.isBlank(imgBase64Code) || StringUtils.isBlank(codes)) {
            return null;
        }
        CaptchaInfo captcha = new CaptchaInfo();
        captcha.setCodes(codes);
        captcha.setBase64Code(imgBase64Code);
        return captcha;
    }

    private String getImageBinary(BufferedImage bi) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "jpg", byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            return new String(new Base64().encode(bytes), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("CaptchaBuilder getImageBinary failure:{}", e.getMessage());
        }
        return null;
    }
}
