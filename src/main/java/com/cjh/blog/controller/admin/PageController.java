package com.cjh.blog.controller.admin;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.controller.BaseController;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.mapper.TContentsMapper;
import com.cjh.blog.pojo.BO.RestResponse;
import com.cjh.blog.pojo.TContents;
import com.cjh.blog.pojo.dto.LogActions;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.pojo.dto.Types;
import com.cjh.blog.service.IContentService;
import com.cjh.blog.service.ILogService;
import com.cjh.blog.service.impl.CommentServiceImpl;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 页面管理
 * @author CJH
 * on 2019/3/19
 */
@Controller
@RequestMapping(value = "/admin/page")
@Transactional(rollbackFor = MyException.class)
public class PageController extends BaseController {

    @Autowired
    private IContentService contentService ;


    @Autowired
    private ILogService logService;

    /**
     * 页面列表
     * @param request
     * @return
     */
    @GetMapping(value = {"","/"})
    public String index(HttpServletRequest request){

        Example example = new Example(TContents.class);
        // 根据创建时间降序排列
        example.orderBy(Table.Contents.created.name()).desc();
        // 内容类型是 页面
        example.createCriteria().andEqualTo(Table.Contents.type.name(), Types.PAGE);

        PageInfo<TContents> articlesPages = contentService.getContentByExample(example, 1, WebConst.MAX_POSTS);
        request.setAttribute("articles", articlesPages);

        return getAdminView("page_list");
    }


    /**
     * 页面编辑
     * @param request
     * @return
     */
    @GetMapping(value = "/{cid}")
    public String editPage(HttpServletRequest request, @PathVariable String cid){

        TContents contents = contentService.getContentByIdOrSlug(cid);

        request.setAttribute("contents", contents);

        return getAdminView("page_edit");
    }


    /**
     * 新建页面
     * @return
     */
    @GetMapping(value = "/new")
    public String createPage(){

        return getAdminView("page_edit");
    }


    /**
     * 新建页面
     * @param request
     * @param page
     * @return
     */
    @PostMapping(value = "/publish")
    @ResponseBody
    public RestResponse publishPage(HttpServletRequest request,TContents page){

        if (page.getAllowComment() == null){
            page.setAllowComment(true);
        }

        if (page.getAllowPing() == null){
            page.setAllowPing(true);
        }

        if (page.getAllowFeed() == null){
            page.setAllowFeed(true);
        }

        page.setAuthorId(this.getUid(request));
        page.setType(Types.PAGE.getType());

        // 插入
        String result = contentService.publish(page);

        if (!WebConst.SUCCESS_RESULT.equals(result)) {
            return RestResponse.fail(result);
        }

        return RestResponse.ok();
    }


    /**
     * 修改页面
     * @param request
     * @param page
     * @return
     */
    @PostMapping(value = "/modify")
    @ResponseBody
    public RestResponse modifyPage(HttpServletRequest request, TContents page){
        if (page.getAllowComment() == null){
            page.setAllowComment(true);
        }

        if (page.getAllowPing() == null){
            page.setAllowPing(true);
        }

        if (page.getAllowFeed() == null){
            page.setAllowFeed(true);
        }
        page.setAuthorId(this.getUid(request));
        page.setType(Types.PAGE.getType());

        String result = contentService.updateArticle(page);
        if (!WebConst.SUCCESS_RESULT.equals(result)) {
            return RestResponse.fail(result);
        }

        return RestResponse.ok();

    }


    /**
     * 删除页面
     * @param request
     * @param cid 页面id
     * @return
     */
    @RequestMapping(value = "/delete")
    @ResponseBody
    public RestResponse delete(HttpServletRequest request, @RequestParam int cid){
        String result = contentService.deleteByCid(cid);
        logService.insertLog(LogActions.DEL_ARTICLE.getAction(), cid+"", request.getRemoteAddr(), this.getUid(request));
        if (!WebConst.SUCCESS_RESULT.equals(result)){
            return RestResponse.fail(result);
        }
        return RestResponse.ok();
    }

}
