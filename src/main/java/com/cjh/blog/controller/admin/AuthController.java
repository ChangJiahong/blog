package com.cjh.blog.controller.admin;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.controller.BaseController;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.pojo.BO.RestResponse;
import com.cjh.blog.pojo.TUsers;
import com.cjh.blog.pojo.dto.LogActions;
import com.cjh.blog.service.ILogService;
import com.cjh.blog.service.IUserService;
import com.cjh.blog.utils.TaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


/**
 * 用户登录
 * @author CJH
 * on 2019/3/14
 */
@Controller
@RequestMapping("/admin")
@Transactional(rollbackFor = MyException.class)
public class AuthController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);


    /**
     * 用户服务
     */
    @Autowired
    private IUserService userService;

    /**
     * 日志
     */
    @Autowired
    private ILogService logService;

    @GetMapping(value = "/login")
    public String login(){
        return getAdminView("login");
    }


    @PostMapping(value = "/login")
    @ResponseBody
    public RestResponse doLogin(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam(required = false) String remeber_me,
                                HttpServletRequest request,
                                HttpServletResponse response){

        String errorKey = "login_error_count";
        Integer error_c = cache.get(errorKey);

        try {
            // 登录
            TUsers user = userService.login(username,password);

            // 设置session
            request.getSession().setAttribute(WebConst.LOGIN_SESSION_KEY, user);

            if (remeber_me != null){
                // 设置记住
                TaleUtils.setCookie(response, user.getUid());
            }

            logService.insertLog(LogActions.LOGIN.getAction(), null, request.getRemoteAddr(), user.getUid());

        }catch (Exception e){

            error_c = error_c == null ? 1 : error_c + 1;

            if (error_c > 3){
                return RestResponse.fail("您输入密码已经错误超过3次，请10分钟后尝试");
            }
            // 设置缓存 10分钟
            cache.set(errorKey, error_c ,10 * 60);
            String msg = "登录失败";
            if (e instanceof MyException){
                msg = e.getMessage();
            } else {
                LOGGER.error(msg, e);
            }
            return RestResponse.fail(msg);

        }

        return RestResponse.ok();
    }


    @PostMapping(value = "/register")
    @ResponseBody
    public RestResponse register(HttpServletRequest request, TUsers user){
        try {
            user = userService.register(user);
        }catch (MyException e){
            return RestResponse.fail(e.getMessage());
        }
        if (user != null){
            return RestResponse.ok();
        }
        return RestResponse.fail();
    }

    @PostMapping(value = "/disableUser")
    @ResponseBody
    public RestResponse disableUserById(HttpServletRequest request, @RequestParam Integer uId, @RequestParam Boolean disable){
        TUsers user = this.user(request);
        if (user!= null){
            if ("superAdmin".equals(user.getGroupName())){
                userService.disableById(uId, disable);
                return RestResponse.ok();
            }
        }

        return RestResponse.fail();
    }

    @PostMapping(value = "/deleteUser")
    @ResponseBody
    public RestResponse deleteUserById(HttpServletRequest request, @RequestParam(value = "uId") Integer uId){
        TUsers user = this.user(request);
        if (user!= null){
            if ("superAdmin".equals(user.getGroupName())){
                userService.delById(uId);
                return RestResponse.ok();
            }
        }

        return RestResponse.fail();
    }

    @PostMapping(value = "/users")
    public RestResponse getUsers(HttpServletRequest request){
        TUsers user = this.user(request);
        if (user == null){
            return RestResponse.fail();
        }

        if ("superAdmin".equals(user.getGroupName())){
            List<TUsers> usersList = userService.getUsersByGroup("user");
            return RestResponse.ok(usersList);
        }

        return RestResponse.fail("权限不足！");
    }

    /**
     * 注销
     * @param request
     * @param session
     * @param response
     */
    @RequestMapping(value = "/logout")
    public void logout(HttpServletRequest request,
                                 HttpSession session,
                                 HttpServletResponse response){
        session.removeAttribute(WebConst.LOGIN_SESSION_KEY);
        Cookie cookie = new Cookie( WebConst.USER_IN_COOKIE, "");
        cookie.setValue(null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        try{
            response.sendRedirect(request.getContextPath()+"/admin/login");
        } catch (IOException e){
            e.printStackTrace();
            LOGGER.error("注销失败", e);
        }
    }
}
