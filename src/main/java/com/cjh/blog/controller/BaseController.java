package com.cjh.blog.controller;

import com.cjh.blog.pojo.TUsers;
import com.cjh.blog.utils.MapCache;
import com.cjh.blog.utils.TaleUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 控制类的模板
 * @author CJH
 * on 2019/3/13
 */

public abstract class BaseController {

    /**
     * 主题地址
     */
    public static String THEME = "themes/default";

    /**
     * 管理员地址
     */
    public static String ADMIN = "admin";

    /**
     * 网站缓存
     */
    protected MapCache cache = MapCache.single();

    /**
     * 获取页面路径
     * @param viewName
     * @return
     */
    public String getView(String viewName){
        return THEME + "/" + viewName;
    }

    /**
     * 获取 管理员页面路径
     * @param viewName
     * @return
     */
    public String getAdminView(String viewName){
        return ADMIN +"/"+ viewName;
    }


    /**
     * 设置标题
     * @param request
     * @param title
     * @return
     */
    public BaseController title(HttpServletRequest request, String title){
        request.setAttribute("title",title);
        return this;
    }

    /**
     * 设置网页关键字
     * @param request
     * @param keywords
     * @return
     */
    public BaseController keywords(HttpServletRequest request, String keywords) {
        request.setAttribute("keywords", keywords);
        return this;
    }


    public String get_404() {
        return "comm/error_404";
    }

    /**
     * 获取请求绑定的登录对象
     * @param request
     * @return
     */
    public TUsers user(HttpServletRequest request) {
        return TaleUtils.getLoginUser(request);
    }

    public Integer getUid(HttpServletRequest request){
        return this.user(request).getUid();
    }

}
