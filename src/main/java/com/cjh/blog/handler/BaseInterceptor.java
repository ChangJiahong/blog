package com.cjh.blog.handler;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.pojo.TOptions;
import com.cjh.blog.pojo.TUsers;
import com.cjh.blog.pojo.dto.Types;
import com.cjh.blog.service.IOptionService;
import com.cjh.blog.service.IUserService;
import com.cjh.blog.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.join;

/**
 * url 拦截器
 * @author CJH
 * on 2019/3/13
 */
@Component
public class BaseInterceptor implements HandlerInterceptor {

    private static final Logger LOGGE = LoggerFactory.getLogger(BaseInterceptor.class);
    private static final String USER_AGENT = "user-agent";


    @Autowired
    private IUserService userService;

    @Autowired
    private IOptionService optionService;

    @Resource
    private Commons commons;

    private MapCache cache = MapCache.single();

    @Resource
    private AdminCommons adminCommons;

    /**
     * 开始拦截 调用处理程序之前
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {


        String contextPath = request.getContextPath();

        String uri = request.getRequestURI();

        LOGGE.info("UserAgent: {}", request.getHeader(USER_AGENT));
        LOGGE.info("用户访问地址: {}, 来路地址: {}", uri, IPKit.getIpAddrByRequest(request));

        // 从Session获取用户信息 获取session
        TUsers user = TaleUtils.getLoginUser(request);

        if (user == null){
            // 如果user 是 null 表示未登录过
            // 查看本地cookie 是否已记住
            // 获取 用户id
            // 对方cookie 可能是伪造的，欺骗服务器
            Integer uid = TaleUtils.getCookieUid(request);
            if (uid != null){
                // 自动登录
                user = userService.getUserById(uid);
                request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY, user);
                LOGGE.info("用户 自动登录");
            }
        }

        if (uri.startsWith(contextPath+"/admin")
                && !uri.startsWith(contextPath + "/admin/login")
                && user == null){
            // 未登录 跳转到登录页面
            response.sendRedirect(request.getContextPath() + "/admin/login");
//            LOGGE.info("拒绝访问");
            return false;
        }
//        LOGGE.info("允许访问");

        //设置get请求的token
        // 评论时用到
        // 浏览文章才生成 _csrf_token
        if (request.getMethod().equals("GET")) {
            String csrf_token = UUID.UU64();
            // 默认存储30分钟
            cache.hset(Types.CSRF_TOKEN.getType(), csrf_token, uri, 30 * 60);
            request.setAttribute("_csrf_token", csrf_token);
        }

        return true;
    }

    /**
     * 结束 调用处理程序之后
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

        request.setAttribute("commons", commons);
        request.setAttribute("adminCommons", adminCommons);

        List<TOptions> optionsList = optionService.getOptions();
        Map<String, String> options = new HashMap<>();
        optionsList.forEach((option) -> {
            options.put(option.getName(), option.getValue());
        });

        WebConst.initConfig = options;

        // 备案号
        TOptions ov = new TOptions();
        ov.setValue(WebConst.initConfig.get("site_record"));
        request.setAttribute("option", ov);

    }

    /**
     * 视图渲染之后
     * @param request
     * @param response
     * @param handler
     * @param ex
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
