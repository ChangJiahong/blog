package com.cjh.blog.controller.admin;

import com.cjh.blog.controller.BaseController;
import com.cjh.blog.pojo.BO.RestResponse;
import com.cjh.blog.pojo.TMetas;
import com.cjh.blog.pojo.dto.Types;
import com.cjh.blog.service.IMetaService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author CJH
 * on 2019/3/22
 */

@Controller
@RequestMapping("/admin/links")
public class LinkController extends BaseController {


    private static final Logger LOGGER = LoggerFactory.getLogger(LinkController.class);
    @Autowired
    private IMetaService metaService ;

    @GetMapping(value = {"","/"})
    public String index(HttpServletRequest request){

        List<TMetas> links =  metaService.getMetasByType(Types.LINK.getType());

        request.setAttribute("links", links);
        return getAdminView("links");
    }


    @PostMapping(value = "/save")
    @ResponseBody
    public RestResponse saveOrUpdate(@RequestParam String title, @RequestParam String url,
                                     @RequestParam String logo, @RequestParam Integer mid,
                                     @RequestParam(defaultValue = "0") int sort){

        try {

            TMetas link = new TMetas();
            link.setName(title);
            link.setSlug(url);
            link.setDescription(logo);
            link.setSort(sort);
            link.setType(Types.LINK.getType());
            if (mid == null) {
                metaService.saveMeta(link);
            } else {
                link.setMid(mid);
                metaService.updateById(link);
            }

        } catch (Exception e){
            String msg = "友链保存失败";
            LOGGER.error(msg, e);
            return RestResponse.fail(msg);
        }


        return RestResponse.ok();
    }

    @RequestMapping(value = "delete")
    @ResponseBody
    public RestResponse delete(@RequestParam int mid) {
        try {
            metaService.delete(mid);
        } catch (Exception e) {
            String msg = "友链删除失败";
            LOGGER.error(msg, e);
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }

}
