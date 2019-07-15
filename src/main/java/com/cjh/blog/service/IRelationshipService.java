package com.cjh.blog.service;

import com.cjh.blog.pojo.TRelationships;

import java.util.List;

/**
 * @author CJH
 * on 2019/3/15
 */

public interface IRelationshipService {

    /**
     *
     * @param cid
     * @param mid
     * @return
     */
    long countById(Integer cid, Integer mid);

    /**
     * 插入
     * @param relationship
     */
    void insert(TRelationships relationship);

    /**
     * 删除
     * @param cid
     * @param mid
     */
    void deleteById(Integer cid, Integer mid);

    /**
     * by id
     * @param cid 文章 id
     * @param mid
     * @return
     */
    List<TRelationships> getRelationshipById(Integer cid, Integer mid);
}
