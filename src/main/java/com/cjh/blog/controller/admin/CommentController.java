package com.cjh.blog.controller.admin;

import com.cjh.blog.controller.BaseController;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.pojo.BO.RestResponse;
import com.cjh.blog.pojo.TComments;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.service.ICommentService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;

/**
 * 评论管理
 * @author CJH
 * on 2019/3/19
 */

@Controller
@RequestMapping(value = "/admin/comments")
@Transactional(rollbackFor = MyException.class)
public class CommentController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private ICommentService commentService ;

    @GetMapping(value = {"","/"})
    public String index(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "15") int limit, HttpServletRequest request){

        Example example = new Example(TComments.class);
        example.orderBy(Table.Comments.coid.name()).desc();
        // 不是本人评论
        example.createCriteria().andNotEqualTo(Table.Comments.authorId.name(), this.getUid(request));

        PageInfo<TComments> commentsPage = commentService.getCommentsByExample(example, page, limit);
        request.setAttribute("comments", commentsPage);

        return getAdminView("comment_list");
    }


    @PostMapping(value = "/delete")
    @ResponseBody
    public RestResponse delete(@RequestParam Integer coid){

        try {

            TComments comment = commentService.getCommentsById(coid);

            if (comment == null) {
                return RestResponse.fail("不存在该评论");
            }

            commentService.delete(coid, comment.getCid());
        } catch (Exception e){
            String msg = "删除失败";
            LOGGER.error(msg, e);
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }


    /**
     * 设置 评论 状态
     * @param coid
     * @param status
     * @return
     */
    @PostMapping(value = "/status")
    @ResponseBody
    public RestResponse setStatus(@RequestParam Integer coid, @RequestParam String status){
        try{
            TComments comment = commentService.getCommentsById(coid);

            if (comment == null) {
                return RestResponse.fail("不存在该评论");
            }
            comment.setStatus(status);

            commentService.updateById(comment);
        } catch (Exception e){
            String msg = "修改失败";
            LOGGER.error(msg, e);
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();
    }
}
