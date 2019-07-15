package com.cjh.blog.service;

import com.cjh.blog.pojo.BO.Archive;
import com.cjh.blog.pojo.BO.BackResponse;
import com.cjh.blog.pojo.BO.Statistics;
import com.cjh.blog.pojo.TComments;
import com.cjh.blog.pojo.TContents;

import java.util.List;

/**
 * @author CJH
 * on 2019/3/14
 */

public interface ISiteService {

    /**
     * 最近的评论
     * @param i
     * @return
     */
    List<TComments> recentComments(int i);


    /**
     * 最近发布的文章
     * @param i
     * @return
     */
    List<TContents> recentContents(int i);

    /**
     * 获取统计数据
     * @return
     */
    Statistics getStatistics();


    /**
     * 获取归档
     * @return
     */
    List<Archive> getArchives();
}
