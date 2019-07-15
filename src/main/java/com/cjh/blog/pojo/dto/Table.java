package com.cjh.blog.pojo.dto;

/**
 * @author CJH
 * on 2019/3/13
 */

public class Table {

    public enum Attach{
        /**
         * 主键id
         */
        id,
        /**
         * name
         */
        fname,
        /**
         * ftype
         */
        ftype,
        /**
         * fkey
         */
        fkey,
        /**
         * authorId
         */
        authorId,
        /**
         * created
         */
        created;
    }

    public enum Contents{
        /**
         * cid
         */
        cid,

        title,

        slug,

        created,

        modified,

        authorId,

        type,

        status,

        tags,

        categories,

        hits,

        commentsNum,

        allowComment,

        allowPing,

        allowFeed,

        /**
         * 内容文字
         */
        content;
    }

    public enum Users{
        /**
         * id
         */
        uid,
        username,
        password,
        email,
        home_url,
        screenName,
        created,
        activated,
        logged,
        groupName,
        disable;
    }

    public enum Logs{
        /**
         * id
         */
        id,
        action,
        data,
        authorId,
        ip,
        created;
    }

    public enum Comments{
        /**
         *  coid 评论主键
         */
        coid,
        /**
         * 文章主键
         */
        cid,
        created,
        author,
        authorId,
        ownerId,
        mail,
        url,
        ip,
        agent,
        content,
        type,
        status,
        parent;
    }

    public enum Meta{
        /**
         * mid
         */
        mid,
        name,
        slug,
        type,
        description,
        sort,
        parent;
    }

    public enum Ralationship{
        /**
         * cid
         */
        cid,
        mid;
    }

}
