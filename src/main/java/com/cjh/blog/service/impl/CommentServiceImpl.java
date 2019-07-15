package com.cjh.blog.service.impl;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.mapper.TCommentsMapper;
import com.cjh.blog.mapper.TContentsMapper;
import com.cjh.blog.pojo.TComments;
import com.cjh.blog.pojo.TContents;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.service.ICommentService;
import com.cjh.blog.service.IContentService;
import com.cjh.blog.utils.DateKit;
import com.cjh.blog.utils.PatternKit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javafx.scene.control.Tab;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author CJH
 * on 2019/3/14
 */
@Service
public class CommentServiceImpl implements ICommentService {


    @Autowired
    private TCommentsMapper commentsMapper ;

    @Autowired
    private IContentService contentService ;


    /**
     * 根据文章id 获取文章的评论
     *
     * @param cid    文章id
     * @param cp     评论页码
     * @param cpSize 每页大小
     * @return
     */
    @Override
    public PageInfo<TComments> getCommentsByContentId(Integer cid, Integer cp, int cpSize) {

        if (cid != null){
            PageHelper.startPage(cp, cpSize);
            Example example = new Example(TComments.class);
            example.orderBy(Table.Comments.created.name()).desc();
            example.createCriteria().andEqualTo(Table.Comments.cid.name(), cid)
                    .andEqualTo(Table.Comments.parent.name(), 0)
                    .andIsNotNull(Table.Comments.status.name())
                    .andEqualTo(Table.Comments.status.name(), "approved");

            List<TComments> comments = commentsMapper.selectByExample(example);

            PageInfo<TComments> commentsPageInfo = new PageInfo<>(comments);

            return commentsPageInfo;
        }

        return null;
    }

    /**
     * 根据 example 获取
     *
     * @param example
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageInfo<TComments> getCommentsByExample(Example example, int page, int limit) {

        PageHelper.startPage(page, limit);
        List<TComments> comments = commentsMapper.selectByExample(example);
        PageInfo<TComments> pageInfo = new PageInfo<>(comments);

        return pageInfo;
    }

    /**
     * 根据id获取评论
     *
     * @param coid
     * @return
     */
    @Override
    public TComments getCommentsById(Integer coid) {
        if (coid != null){
            return commentsMapper.selectByPrimaryKey(coid);
        }
        return null;
    }

    /**
     * 根据 文章id 评论id删除
     *
     * @param coid
     * @param cid
     */
    @Override
    @Transactional
    public void delete(Integer coid, Integer cid) {
        if (coid == null){
            throw new MyException("主键为空");
        }
        commentsMapper.deleteByPrimaryKey(coid);

        // 更新文章评论数
        TContents content = contentService.getContentByIdOrSlug(cid.toString());
        if (content != null && content.getCommentsNum() > 0){
            TContents temp = new TContents();
            temp.setCid(cid);
            temp.setCommentsNum(content.getCommentsNum());
            contentService.updateContentByCid(temp);
        }

    }

    /**
     * 修改操作
     *
     * @param comment
     */
    @Override
    @Transactional
    public void updateById(TComments comment) {
        if (comment != null && comment.getCid() != null){
            commentsMapper.updateByPrimaryKeySelective(comment);
        }
    }

    /**
     * 发布评论
     *
     * @param comments
     * @return
     */
    @Override
    public String insertComment(TComments comments) {

        if (null == comments) {
            return "评论对象为空";
        }
        if (StringUtils.isBlank(comments.getAuthor())) {
            comments.setAuthor("热心网友");
        }
        if (StringUtils.isNotBlank(comments.getMail()) && !PatternKit.isEmail(comments.getMail())) {
            return "请输入正确的邮箱格式";
        }
        if (StringUtils.isBlank(comments.getContent())) {
            return "评论内容不能为空";
        }
        if (comments.getContent().length() < 5 || comments.getContent().length() > 2000) {
            return "评论字数在5-2000个字符";
        }
        if (null == comments.getCid()) {
            return "评论文章不能为空";
        }
        TContents contents = contentService.getContentByIdOrSlug(String.valueOf(comments.getCid()));
        if (null == contents) {
            return "不存在的文章";
        }
        //所有者id 即文章发布者的id
        comments.setOwnerId(contents.getAuthorId());
        // 评论状态 未审核
        comments.setStatus("not_audit");
        // 评论时间
        comments.setCreated(DateKit.getCurrentUnixTime());

        commentsMapper.insertSelective(comments);

        TContents temp = new TContents();
        temp.setCid(contents.getCid());
        temp.setCommentsNum(contents.getCommentsNum() + 1);
        // 更新评论数
        contentService.updateContentByCid(temp);

        return WebConst.SUCCESS_RESULT;

    }
}
