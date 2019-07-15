package com.cjh.blog.pojo.BO;

import com.cjh.blog.pojo.TContents;

import java.io.Serializable;
import java.util.List;

/**
 * @author CJH
 * on 2019/3/22
 */

public class Archive implements Serializable {


    private String date;
    private String count;
    private List<TContents> articles;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public List<TContents> getArticles() {
        return articles;
    }

    public void setArticles(List<TContents> articles) {
        this.articles = articles;
    }
}
