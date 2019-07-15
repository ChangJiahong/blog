package com.cjh.blog.service;

import com.cjh.blog.pojo.TMetas;
import com.cjh.blog.pojo.dto.Meta;

import java.util.List;

/**
 * @author CJH
 * on 2019/3/14
 */

public interface IMetaService {

    /**
     * 保存 标签 或者 分类
     * @param cid
     * @param names
     * @param type
     */
    void saveTagsOrCategorys(Integer cid, String names, String type);

    /**
     * 根据类型 获取 分类 or 标签
     * @param type
     * @return
     */
    List<TMetas> getMetasByType(String type);

    /**
     * 获取 分类 列表
     * @param type
     * @param orderBy 排序
     * @param maxPosts
     */
    List<Meta> getCategoryList(String type, String orderBy, int maxPosts);


    /**
     * 修改 分类标签 或者 新建
     * @param type
     * @param cname
     * @param mid
     */
    void saveCategoryOrUpdateById(String type, String cname, Integer mid);


    /**
     * delete by mid
     * @param mid
     */
    void delete(int mid);

    /**
     * 保存
     * @param link
     */
    void saveMeta(TMetas link);

    /**
     * 修改
     * @param link
     */
    void updateById(TMetas link);

    /**
     * 获取分类信息
     * @param type
     * @param name
     * @return
     */
    Meta getMetaByTypeAndName(String type, String name);
}
