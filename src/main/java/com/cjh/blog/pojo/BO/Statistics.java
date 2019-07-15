package com.cjh.blog.pojo.BO;

import java.io.Serializable;

/**
 * @author CJH
 * on 2019/3/14
 */

public class Statistics implements Serializable {

    /**
     * 文章数
     */
    private Long articles;
    /**
     * 评论数
     */
    private Long comments;
    /**
     * 友链数
     */
    private Long links;
    /**
     * 附件数
     */
    private Long attachs;

    public Long getArticles() {
        return articles;
    }

    public void setArticles(Long articles) {
        this.articles = articles;
    }

    public Long getComments() {
        return comments;
    }

    public void setComments(Long comments) {
        this.comments = comments;
    }

    public Long getLinks() {
        return links;
    }

    public void setLinks(Long links) {
        this.links = links;
    }

    public Long getAttachs() {
        return attachs;
    }

    public void setAttachs(Long attachs) {
        this.attachs = attachs;
    }

    @Override
    public String toString() {
        return "Statistics{" +
                "articles=" + articles +
                ", comments=" + comments +
                ", links=" + links +
                ", attachs=" + attachs +
                '}';
    }
}
