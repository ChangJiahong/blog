package com.cjh.blog.controller.admin;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.controller.BaseController;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.pojo.BO.RestResponse;
import com.cjh.blog.pojo.TOptions;
import com.cjh.blog.pojo.TUsers;
import com.cjh.blog.pojo.dto.LogActions;
import com.cjh.blog.service.ILogService;
import com.cjh.blog.service.IOptionService;
import com.cjh.blog.service.ISiteService;
import com.cjh.blog.service.IUserService;
import com.cjh.blog.utils.GsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.join;

/**
 * @author CJH
 * on 2019/3/22
 */

@Controller
@RequestMapping("/admin/setting")
public class SettingController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingController.class);


    @Autowired
    private IOptionService optionService;

    @Autowired
    private ILogService logService;




    @GetMapping(value = {"","/"})
    public String index(HttpServletRequest request){

        List<TOptions> optionsList = optionService.getOptions();
        Map<String, String> options = new HashMap<>();
        optionsList.forEach((option) -> {
            options.put(option.getName(), option.getValue());
        });

        if (options.get("site_record") == null) {
            options.put("site_record", "");
        }
        request.setAttribute("options", options);
        return getAdminView("setting");
    }


    /**
     * 保存系统设置
     */
    @PostMapping(value = "")
    @ResponseBody
    public RestResponse saveSetting(@RequestParam(required = false) String site_theme, HttpServletRequest request) {
        try {
            Map<String, String[]> parameterMap = request.getParameterMap();
            Map<String, String> querys = new HashMap<>();
            parameterMap.forEach((key, value) -> {
                querys.put(key, join(value));
            });
            optionService.saveOptions(querys);
            WebConst.initConfig = querys;
            if (StringUtils.isNotBlank(site_theme)) {
                BaseController.THEME = "themes/" + site_theme;
            }
            logService.insertLog(LogActions.SYS_SETTING.getAction(), GsonUtils.toJsonString(querys), request.getRemoteAddr(), this.getUid(request));
            return RestResponse.ok();
        } catch (Exception e) {
            String msg = "保存设置失败";
            return RestResponse.fail(msg);
        }
    }



}
