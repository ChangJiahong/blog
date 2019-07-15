package com.cjh.blog.pojo;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "t_contents")
public class TContents implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cid;

    private String title;

    private String slug;

    private Integer created;

    private Integer modified;

    @Column(name = "author_id")
    private Integer authorId;

    private String type;

    private String status;

    private String tags;

    private String categories;

    private Integer hits;

    @Column(name = "comments_num")
    private Integer commentsNum;

    @Column(name = "allow_comment")
    private Boolean allowComment;

    @Column(name = "allow_ping")
    private Boolean allowPing;

    @Column(name = "allow_feed")
    private Boolean allowFeed;

    /**
     * 内容文字
     */
    private String content;

    private static final long serialVersionUID = 1L;

    /**
     * @return cid
     */
    public Integer getCid() {
        return cid;
    }

    /**
     * @param cid
     */
    public void setCid(Integer cid) {
        this.cid = cid;
    }

    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    /**
     * @return slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * @param slug
     */
    public void setSlug(String slug) {
        this.slug = slug == null ? null : slug.trim();
    }

    /**
     * @return created
     */
    public Integer getCreated() {
        return created;
    }

    /**
     * @param created
     */
    public void setCreated(Integer created) {
        this.created = created;
    }

    /**
     * @return modified
     */
    public Integer getModified() {
        return modified;
    }

    /**
     * @param modified
     */
    public void setModified(Integer modified) {
        this.modified = modified;
    }

    /**
     * @return author_id
     */
    public Integer getAuthorId() {
        return authorId;
    }

    /**
     * @param authorId
     */
    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    /**
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * @return tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * @param tags
     */
    public void setTags(String tags) {
        this.tags = tags == null ? null : tags.trim();
    }

    /**
     * @return categories
     */
    public String getCategories() {
        return categories;
    }

    /**
     * @param categories
     */
    public void setCategories(String categories) {
        this.categories = categories == null ? null : categories.trim();
    }

    /**
     * @return hits
     */
    public Integer getHits() {
        return hits;
    }

    /**
     * @param hits
     */
    public void setHits(Integer hits) {
        this.hits = hits;
    }

    /**
     * @return comments_num
     */
    public Integer getCommentsNum() {
        return commentsNum;
    }

    /**
     * @param commentsNum
     */
    public void setCommentsNum(Integer commentsNum) {
        this.commentsNum = commentsNum;
    }

    /**
     * @return allow_comment
     */
    public Boolean getAllowComment() {
        return allowComment;
    }

    /**
     * @param allowComment
     */
    public void setAllowComment(Boolean allowComment) {
        this.allowComment = allowComment;
    }

    /**
     * @return allow_ping
     */
    public Boolean getAllowPing() {
        return allowPing;
    }

    /**
     * @param allowPing
     */
    public void setAllowPing(Boolean allowPing) {
        this.allowPing = allowPing;
    }

    /**
     * @return allow_feed
     */
    public Boolean getAllowFeed() {
        return allowFeed;
    }

    /**
     * @param allowFeed
     */
    public void setAllowFeed(Boolean allowFeed) {
        this.allowFeed = allowFeed;
    }

    /**
     * 获取内容文字
     *
     * @return content - 内容文字
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置内容文字
     *
     * @param content 内容文字
     */
    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    @Override
    public String toString() {
        return "TContents{" +
                "cid=" + cid +
                ", title='" + title + '\'' +
                ", slug='" + slug + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                ", authorId=" + authorId +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", tags='" + tags + '\'' +
                ", categories='" + categories + '\'' +
                ", hits=" + hits +
                ", commentsNum=" + commentsNum +
                ", allowComment=" + allowComment +
                ", allowPing=" + allowPing +
                ", allowFeed=" + allowFeed +
                ", content='" + content + '\'' +
                '}';
    }
}