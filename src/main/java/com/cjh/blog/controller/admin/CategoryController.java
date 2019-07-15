package com.cjh.blog.controller.admin;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.controller.BaseController;
import com.cjh.blog.pojo.BO.RestResponse;
import com.cjh.blog.pojo.dto.Meta;
import com.cjh.blog.pojo.dto.Types;
import com.cjh.blog.service.IMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 *
 * 分类管理
 * @author CJH
 * on 2019/3/20
 */

@Controller
@RequestMapping(value = "/admin/category")
public class CategoryController extends BaseController {


    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryController.class);


    @Autowired
    private IMetaService metaService;

    @GetMapping(value = {"","/"})
    public String index(HttpServletRequest request){

        List<Meta> categories = metaService.getCategoryList(Types.CATEGORY.getType(),null,WebConst.MAX_POSTS);
        List<Meta> tags = metaService.getCategoryList(Types.TAG.getType(), null, WebConst.MAX_POSTS);

        request.setAttribute("categories", categories);
        request.setAttribute("tags", tags);
        return getAdminView("category");
    }


    /**
     * 修改或新建
     * @return
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public RestResponse createOrUpdate(HttpServletRequest request,
                                       @RequestParam Integer mid,
                                       @RequestParam String cname){
        try {
            System.out.println("middsadada" +mid);
            metaService.saveCategoryOrUpdateById(Types.CATEGORY.getType(), cname, mid);
        } catch (Exception e) {
            String msg = "分类保存失败";
            LOGGER.error(msg, e);
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }


    /**
     * 删除
     * @param mid
     * @return
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public RestResponse delete(@RequestParam int mid){
        try {
            metaService.delete(mid);
        } catch (Exception e) {
            String msg = "删除失败";
            LOGGER.error(msg, e);
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }

}
