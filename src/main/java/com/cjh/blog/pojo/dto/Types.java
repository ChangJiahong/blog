package com.cjh.blog.pojo.dto;

/**
 * 枚举类型
 * @author CJH
 * on 2019/3/13
 */

public enum Types {

    /**
     *  标签
     */
    TAG("tag"),
    /**
     *  文章分类
     */
    CATEGORY("category"),
    /**
     *
     */
    ARTICLE("article"),

    PAGE("page"),

    /**
     * 发布
     */
    PUBLISH("publish"),
    /**
     * 草稿
     */
    DRAFT("draft"),


    LINK("link"),
    IMAGE("image"),
    FILE("file"),
    CSRF_TOKEN("csrf_token"),

    /**
     * 评论评率
     */
    COMMENTS_FREQUENCY("comments:frequency"),

    /**
     * 浏览频率
     */
    HITS_FREQUENCY("hits:frequency"),

    /**
     * 附件存放的URL，默认为网站地址，如集成第三方则为第三方CDN域名
     */
    ATTACH_URL("attach_url"),

    /**
     * 网站要过滤，禁止访问的ip列表
     */
    BLOCK_IPS("site_block_ips");


    private String type;

    public java.lang.String getType() {
        return type;
    }

    public void setType(java.lang.String type) {
        this.type = type;
    }

    Types(java.lang.String type) {
        this.type = type;
    }
}
