package com.cjh.blog.controller.admin;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.controller.BaseController;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.pojo.BO.RestResponse;
import com.cjh.blog.pojo.BO.Statistics;
import com.cjh.blog.pojo.TComments;
import com.cjh.blog.pojo.TContents;
import com.cjh.blog.pojo.TLogs;
import com.cjh.blog.pojo.TUsers;
import com.cjh.blog.pojo.dto.LogActions;
import com.cjh.blog.service.*;
import com.cjh.blog.utils.GsonUtils;
import com.cjh.blog.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.plaf.TableUI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CJH
 * on 2019/3/14
 */

@Controller("adminIndexController")
@RequestMapping("/admin")
@Transactional(rollbackFor = MyException.class)
public class IndexController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);


    /**
     * 网站服务
     */
    @Autowired
    private ISiteService siteService;

    @Autowired
    private ILogService logService;

    @Autowired
    private IUserService userService;


    /**
     * index 页
     * @param request
     * @return
     */
    @GetMapping(value = {"","/index","/index.html"})
    public String index(HttpServletRequest request){
        LOGGER.info("Enter admin index method");
        List<TComments> comments = siteService.recentComments(5);
        List< TContents > contents = siteService.recentContents(5);

        Statistics statistics = siteService.getStatistics();

        // 取最新的5条日志
        List<TLogs> logs = logService.getLogs(1,5 );

        request.setAttribute("comments", comments);
        request.setAttribute("articles", contents);
        request.setAttribute("statistics", statistics);
        request.setAttribute("logs", logs);
        LOGGER.info("Exit admin index method");
        return getAdminView("index");
    }


    /**
     * 个人设置
     * @return
     */
    @GetMapping(value = "/profile")
    public String profile(HttpServletRequest request){
        TUsers user = this.user(request);
        if (user != null) {
            if ("superAdmin".equals(user.getGroupName())) {
                List<TUsers> usersList = userService.getUsersByGroup("user");
                request.setAttribute("userList", usersList);
            }
        }
        return getAdminView("profile");
    }

    /**
     * 保存设置
     * @param screenName
     * @param email
     * @param request
     * @param session
     * @return
     */
    @PostMapping(value = "/profile")
    @ResponseBody
    public RestResponse saveProfile(@RequestParam String screenName,
                                    @RequestParam String email,
                                    HttpServletRequest request,
                                    HttpSession session){
        TUsers user = this.user(request);

        if (user != null){
            TUsers userT = new TUsers();
            userT.setUid(user.getUid());
            userT.setScreenName(screenName);
            userT.setEmail(email);
            userService.updateById(userT);
            logService.insertLog(LogActions.UP_INFO.getAction(), GsonUtils.toJsonString(userT), request.getRemoteAddr(), user.getUid());

            TUsers userS = (TUsers) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
            userS.setScreenName(screenName);
            user.setEmail(email);
            session.setAttribute(WebConst.LOGIN_SESSION_KEY, userS);

        }
        return RestResponse.ok();
    }


    /**
     * 修改密码
     */
    @PostMapping(value = "/password")
    @ResponseBody
    public RestResponse upPwd(@RequestParam String oldPassword,
                              @RequestParam String password,
                              HttpServletRequest request,
                              HttpSession session){
        TUsers user = this.user(request);

        if (user != null){
            if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(password)){
                return RestResponse.fail("请输入完整信息！");
            }
            if (!user.getPassword().equals(TaleUtils.MD5encode(user.getUsername()+oldPassword))){
                return RestResponse.fail("原密码错误！");
            }
            if (password.length() < 6 || password.length() > 14){
                return RestResponse.fail("密码长度在6-14之间");
            }
            try{
                TUsers temp = new TUsers();
                temp.setUid(user.getUid());
                String pwd = TaleUtils.MD5encode(user.getUsername()+password);
                temp.setPassword(pwd);
                userService.updateById(temp);
                logService.insertLog(LogActions.UP_PWD.getAction(), null, request.getRemoteAddr(), user.getUid());

                TUsers userS = (TUsers) session.getAttribute(WebConst.LOGIN_SESSION_KEY);
                userS.setPassword(pwd);
                session.setAttribute(WebConst.LOGIN_SESSION_KEY, userS);
                return RestResponse.ok();
            } catch (Exception e){
                String msg = "密码修改失败";
                if (e instanceof MyException) {
                    msg = e.getMessage();
                } else {
                    LOGGER.error(msg, e);
                }
                return RestResponse.fail(msg);
            }
        }

        return RestResponse.fail();
    }



}
