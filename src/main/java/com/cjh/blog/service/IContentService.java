package com.cjh.blog.service;

import com.cjh.blog.pojo.TContents;
import com.github.pagehelper.PageInfo;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author CJH
 * on 2019/3/13
 */

public interface IContentService {

    /**
     * 获取文章列表
     * @param pageSize 页码
     * @param limit 每页大小
     * @return
     */
    PageInfo<TContents> getContents(int pageSize, int limit);


    /**
     * 根据文章id获取文章
     * @param cid
     * @return
     */
    TContents getContentByIdOrSlug(String cid);

    /**
     * 根据id 更新
     * @param temp
     */
    void updateContentByCid(TContents temp);


    /**
     * 更新例子获取文章
     * @param example
     * @param page
     * @param limit
     * @return
     */
    PageInfo<TContents> getContentByExample(Example example, int page, int limit);

    /**
     * 发布文章
     * @param content
     * @return
     */
    String publish(TContents content);

    /**
     *  修改文章
     * @param content
     * @return
     */
    String updateArticle(TContents content);

    /**
     * 删除文章
     * @param cid
     * @return
     */
    String deleteByCid(Integer cid);

    /**
     * 更新文章分类
     * @param name
     * @param name1
     */
    void updateCategory(String name, String name1);

    /**
     * by id 获取文章
     * @param cid
     * @return
     */
    TContents getContentByCid(Integer cid);

    /**
     * 获取文章列表 by  分类id
     * @param mid
     * @param page
     * @param limit
     * @return
     */
    PageInfo<TContents> getArticlesByMetaId(Integer mid, int page, int limit);

    /**
     * 模糊匹配 文章 by 关键字
     * @param keyword
     * @param page
     * @param limit
     * @return
     */
    PageInfo<TContents> getArticlesByKeyword(String keyword, int page, int limit);
}
