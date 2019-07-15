package com.cjh.blog.utils;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.controller.admin.AttachController;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.pojo.TUsers;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.DatabaseMetaData;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author CJH
 * on 2019/3/13
 */

public class TaleUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaleUtils.class);


    private static String upFilePath ;

    public static void setUpFilePath(String filePath){
        upFilePath = filePath;
    }



    /**
     * 匹配邮箱正则
     */
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    /**
     * 路径正则
     */
    private static final Pattern SLUG_REGEX = Pattern.compile("^[A-Za-z0-9_-]{5,100}$", Pattern.CASE_INSENSITIVE);

    // 使用双重检查锁的单例方式需要添加 volatile 关键字
    private static volatile DataSource newDataSource;



    /**
     * markdown转换为html
     *
     * @param markdown
     * @return
     */
    public static String mdToHtml(String markdown) {
        if (StringUtils.isBlank(markdown)) {
            return "";
        }
        java.util.List<Extension> extensions = Arrays.asList(TablesExtension.create());
        Parser parser = Parser.builder().extensions(extensions).build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().extensions(extensions).build();
        String content = renderer.render(document);
        content = Commons.emoji(content);
        return content;
    }


    /**
     * 返回当前登录用户
     *
     * @return
     */
    public static TUsers getLoginUser(HttpServletRequest request) {
        // 获取会话信息，如果不存在则创建一个
        HttpSession session = request.getSession();
        if (null == session) {
            return null;
        }
        return (TUsers) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
    }

    /**
     * 获取cookie中的用户id
     *
     * @param request
     * @return
     */
    public static Integer getCookieUid(HttpServletRequest request) {
        if (null != request) {
            // 获取cookie
            Cookie cookie = cookieRaw(WebConst.USER_IN_COOKIE, request);
            if (cookie != null && cookie.getValue() != null) {
                try {
                    // 解密得到uid
                    String uid = Tools.deAes(cookie.getValue(), WebConst.AES_SALT);
                    return StringUtils.isNotBlank(uid) && Tools.isNumber(uid) ? Integer.valueOf(uid) : null;
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    /**
     * 从cookies中获取指定cookie
     *
     * @param name    名称
     * @param request 请求
     * @return cookie
     */
    private static Cookie cookieRaw(String name, HttpServletRequest request) {
        Cookie[] servletCookies = request.getCookies();
        if (servletCookies == null) {
            return null;
        }
        for (Cookie c : servletCookies) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    /**
     * 设置记住密码cookie
     *
     * @param response
     * @param uid
     */
    public static void setCookie(HttpServletResponse response, Integer uid) {
        try {
            String val = Tools.enAes(uid.toString(), WebConst.AES_SALT);
            boolean isSSL = false;

            Cookie cookie = new Cookie(WebConst.USER_IN_COOKIE, val);
            cookie.setPath("/");
            // 半小时
            cookie.setMaxAge(60 * 30);
            cookie.setSecure(isSSL);
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * md5加密
     *
     * @param source 数据源
     * @return 加密字符串
     */
    public static String MD5encode(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {
        }
        byte[] encode = messageDigest.digest(source.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte anEncode : encode) {
            String hex = Integer.toHexString(0xff & anEncode);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static String getUploadFilePath() {
        File fileD = new File(upFilePath);
        if (!fileD.exists()){
            fileD.mkdirs();
        }
        return fileD.getAbsolutePath();
    }


    public static boolean isPath(String slug) {
        if (StringUtils.isNotBlank(slug)){
            if (slug.contains("/") || slug.contains(" ") || slug.contains(".")){
                return false;
            }
            Matcher matcher = SLUG_REGEX.matcher(slug);
            return matcher.find();
        }
        return false;
    }

    /**
     * 生成 文件上传后的路径
     * @param fname
     * @return
     */
    public static String getFileKey(String fname) {
        String prefix = "/upload/" + DateKit.dateFormat(new Date(), "yyyy/MM");

        File file = new File(AttachController.FILE_PATH + prefix);
        if (!file.exists()){
            file.mkdirs();
        }
        fname = StringUtils.trimToNull(fname);
        if (fname == null){
            return prefix + "/" + UUID.UU32() + "." + null;
        } else {
            fname = fname.replace("\\", "/");
            // 获取最终的文件名，去除路径信息
            fname = fname.substring(fname.lastIndexOf("/")+1);
            int index  = fname.lastIndexOf(".");
            String ext = null; // 文件后缀名
            if (index > 0){
                ext = StringUtils.trimToNull(fname.substring(index +1 ));
            }
            return prefix + "/" + UUID.UU32() + "." + (ext==null ? null : ext);
        }

    }

    /**
     * 判断是否为图片类型
     * @param stream
     * @return
     */
    public static Boolean isImage(InputStream stream){
        try {
            Image img = ImageIO.read(stream);
            if (img == null || img.getWidth(null) <= 0 || img.getHeight(null) <= 0 ){
                return false;
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 生成随机数
     * @param size
     * @return
     */
    public static String getRandomNumber(int size) {
        String num = "";

        for (int i = 0; i < size; ++i) {
            double a = Math.random() * 9.0D;
            a = Math.ceil(a);
            int randomNum = (new Double(a)).intValue();
            num = num + randomNum;
        }

        return num;
    }


    public static boolean isEmail(String mail) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mail);
        return matcher.find();
    }


    /**
     * 替换HTML脚本 防止 xss注入
     *
     * @param value
     * @return
     */
    public static String cleanXSS(String value) {
        //You'll need to remove the spaces from the html entities below
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        value = value.replaceAll("script", "");
        return value;
    }
}
