package com.cjh.blog.controller.admin;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.controller.BaseController;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.pojo.BO.RestResponse;
import com.cjh.blog.pojo.TContents;
import com.cjh.blog.pojo.TMetas;
import com.cjh.blog.pojo.TUsers;
import com.cjh.blog.pojo.dto.LogActions;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.pojo.dto.Types;
import com.cjh.blog.service.IContentService;
import com.cjh.blog.service.ILogService;
import com.cjh.blog.service.IMetaService;
import com.github.pagehelper.PageInfo;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 文章编辑发布
 * @author CJH
 * on 2019/3/14
 */

@Controller
@RequestMapping("/admin/article")
@Transactional(rollbackFor = MyException.class)
public class ArticleController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private IContentService contentService;

    @Autowired
    private IMetaService metaService;

    @Autowired
    private ILogService logService;

    @GetMapping(value = {"","/"})
    public String index(@RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "limit", defaultValue = "15") int limit,
                        HttpServletRequest request){

        Example example = new Example(TContents.class);
        example.orderBy(Table.Contents.created.name()).desc();
        example.createCriteria().andEqualTo(Table.Contents.type.name(), Types.ARTICLE.getType());

        PageInfo<TContents> articleList = contentService.getContentByExample(example,page, limit);
        request.setAttribute("articles", articleList);
        return this.getAdminView("article_list");
    }


    /**
     * 新建文章
     * @return
     */
    @GetMapping(value = "/publish")
    public String createArticle(HttpServletRequest request){
        List<TMetas> metas = metaService.getMetasByType(Types.CATEGORY.getType());
        request.setAttribute("categories", metas);
        return this.getAdminView("article_edit");
    }


    /**
     * 发布文章
     * @return
     */
    @PostMapping(value = "/publish")
    @ResponseBody
    public RestResponse publishArticle(TContents content, HttpServletRequest request){
        LOGGER.debug("Enter /admin/article/publish");
        TUsers user = this.user(request);
        // 作者id
        content.setAuthorId(user.getUid());
        // 类型是文章
        content.setType(Types.ARTICLE.getType());

        String result = contentService.publish(content);
        if (!WebConst.SUCCESS_RESULT.equals(result)){
            return RestResponse.fail(result);
        }
        return RestResponse.ok();
    }

    /**
     * 查看文章
     * @param cid
     * @param request
     * @return
     */
    @GetMapping(value = "/{cid}")
    public String editArticle(@PathVariable String cid, HttpServletRequest request) {

        TContents contents = contentService.getContentByIdOrSlug(cid);

        request.setAttribute("contents", contents);

        // 分类列表
        List<TMetas> categories = metaService.getMetasByType(Types.CATEGORY.getType());

        request.setAttribute("categories", categories);
        return this.getAdminView("article_edit");

    }


    /**
     * 修改文章
     * @param content
     * @param request
     * @return
     */
    @PostMapping(value = "/modify")
    @ResponseBody
    public RestResponse modifyArticle(TContents content, HttpServletRequest request){
        TUsers user = this.user(request);
        content.setAuthorId(user.getUid());
        // 文章类型
        content.setType(Types.ARTICLE.getType());
        String result = contentService.updateArticle(content);
        if (!WebConst.SUCCESS_RESULT.equals(result)){
            return RestResponse.fail(result);
        }
        return RestResponse.ok();
    }


    /**
     * 删除文章 根据id
     * @param cid
     * @param request
     * @return
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public RestResponse delete(@RequestParam Integer cid, HttpServletRequest request){

        String result = contentService.deleteByCid(cid);
        logService.insertLog(LogActions.DEL_ARTICLE.getAction(), cid+"", request.getRemoteAddr(), this.getUid(request));
        if (!WebConst.SUCCESS_RESULT.equals(result)){
            return RestResponse.fail(result);
        }
        return RestResponse.ok();
    }


}
