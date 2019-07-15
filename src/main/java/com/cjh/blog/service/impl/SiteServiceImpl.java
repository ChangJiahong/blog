package com.cjh.blog.service.impl;

import com.cjh.blog.controller.admin.AttachController;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.mapper.TAttachMapper;
import com.cjh.blog.mapper.TCommentsMapper;
import com.cjh.blog.mapper.TContentsMapper;
import com.cjh.blog.mapper.TMetasMapper;
import com.cjh.blog.pojo.BO.Archive;
import com.cjh.blog.pojo.BO.BackResponse;
import com.cjh.blog.pojo.BO.Statistics;
import com.cjh.blog.pojo.TAttach;
import com.cjh.blog.pojo.TComments;
import com.cjh.blog.pojo.TContents;
import com.cjh.blog.pojo.TMetas;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.pojo.dto.Types;
import com.cjh.blog.service.ISiteService;
import com.cjh.blog.utils.DateKit;
import com.cjh.blog.utils.TaleUtils;
import com.cjh.blog.utils.ZipUtils;
import com.cjh.blog.utils.backup.Backup;
import com.github.pagehelper.PageHelper;
import javafx.scene.control.Tab;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author CJH
 * on 2019/3/14
 */

@Service
public class SiteServiceImpl implements ISiteService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SiteServiceImpl.class);

    @Autowired
    private TCommentsMapper commentsMapper;

    @Autowired
    private TContentsMapper contentsMapper;

    @Autowired
    private TAttachMapper attachMapper;

    @Autowired
    private TMetasMapper metasMapper;

    /**
     * 最近的评论
     *
     * @param i
     * @return
     */
    @Override
    public List<TComments> recentComments(int i) {
        LOGGER.debug("Enter recentComments method:limit={}",i);

        if (i < 0 || i > 10){
            i = 10;
        }
        Example example = new Example(TComments.class);

        example.setOrderByClause(Table.Comments.created.name()+" desc");

        PageHelper.startPage(1,i);
        List<TComments> comments = commentsMapper.selectByExample(example);
        LOGGER.debug("Exit recentComments method");
        return comments;
    }

    /**
     * 最近发布的文章
     *
     * @param i
     * @return
     */
    @Override
    public List<TContents> recentContents(int i) {
        LOGGER.debug("Enter recentContents method");
        if (i < 0 || i> 10){
            i = 10;
        }
        Example example = new Example(TComments.class);
        example.setOrderByClause(Table.Contents.created.name()+" desc");

        PageHelper.startPage(1,i);
        List<TContents> contents = contentsMapper.selectByExample(example);
        LOGGER.debug("Exit recentContents method");
        return contents;
    }

    /**
     * 获取统计数据
     *
     * @return
     */
    @Override
    public Statistics getStatistics() {
        LOGGER.debug("Enter getStatistics method");
        Statistics statistics = new Statistics();
        Example example = new Example(TContents.class);
        // 是文章类型 并且已发表的
        example.createCriteria().andEqualTo(Table.Contents.type.name(), Types.ARTICLE.getType())
                .andEqualTo(Table.Contents.status.name(), Types.PUBLISH.getType());
        long articles = contentsMapper.selectCountByExample(example);

        /**
         * 评论
         */
        Example example1 = new Example(TComments.class);
        long comments = commentsMapper.selectCountByExample(example1);

        /**
         * 附件
         */
        Example example2 = new Example(TAttach.class);
        long attachs = attachMapper.selectCountByExample(example2);

        /**
         * 友链
         */
        Example example3 = new Example(TMetas.class);
        // 友链类型是 link
        example3.createCriteria().andEqualTo(Table.Meta.type.name(), Types.LINK.getType());
        long links = metasMapper.selectCountByExample(example3);


        statistics.setArticles(articles);
        statistics.setAttachs(attachs);
        statistics.setComments(comments);
        statistics.setLinks(links);

        LOGGER.debug("Exit getStatistics method");

        return statistics;
    }

    /**
     * 获取归档
     *
     * @return
     */
    @Override
    public List<Archive> getArchives() {
        LOGGER.debug("Enter getArchives method");

        // 同月份 文章数
        List<Archive> archives = contentsMapper.findReturnArchive();

        if (null != archives) {
            archives.forEach(archive -> {
                Example example = new Example(TContents.class);
                Example.Criteria criteria = example.createCriteria()
                        .andEqualTo(Table.Contents.type.name(),Types.ARTICLE.getType())
                        .andEqualTo(Table.Contents.status.name(),Types.PUBLISH.getType());

                example.orderBy(Table.Contents.created.name()).desc();

                String date = archive.getDate();
                Date sd = DateKit.dateFormat(date, "yyyy年MM月");
                int start = DateKit.getUnixTimeByDate(sd);
                int end = DateKit.getUnixTimeByDate(DateKit.dateAdd(DateKit.INTERVAL_MONTH, sd, 1)) - 1;

                // 获取在此时间段内的文章
                criteria.andGreaterThan(Table.Contents.created.name(), start);
                criteria.andLessThan(Table.Contents.created.name(), end);

                List<TContents> contentss = contentsMapper.selectByExample(example);
                archive.setArticles(contentss);
            });
        }
        LOGGER.debug("Exit getArchives method");
        return archives;
    }
}
