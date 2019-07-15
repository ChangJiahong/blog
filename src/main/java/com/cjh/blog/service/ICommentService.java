package com.cjh.blog.service;

import com.cjh.blog.pojo.TComments;
import com.github.pagehelper.PageInfo;
import tk.mybatis.mapper.entity.Example;

/**
 * 评论
 * @author CJH
 * on 2019/3/14
 */

public interface ICommentService {

    /**
     * 根据文章id 获取文章的评论
     * @param cid 文章id
     * @param cp 评论页码
     * @param cpSize 每页大小
     * @return
     */
    PageInfo<TComments> getCommentsByContentId(Integer cid, Integer cp, int cpSize);


    /**
     * 根据 example 获取
     * @param example
     * @param page
     * @param limit
     * @return
     */
    PageInfo<TComments> getCommentsByExample(Example example, int page, int limit);

    /**
     * 根据id获取评论
     * @param coid
     * @return
     */
    TComments getCommentsById(Integer coid);

    /**
     * 根据 文章id 评论id删除
     * @param coid
     * @param cid
     */
    void delete(Integer coid, Integer cid);


    /**
     * 修改操作
     * @param comment
     */
    void updateById(TComments comment);


    /**
     * 发布评论
     * @param comments
     * @return
     */
    String insertComment(TComments comments);
}
