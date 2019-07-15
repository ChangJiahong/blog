package com.cjh.blog.service;

import com.cjh.blog.pojo.TAttach;
import com.github.pagehelper.PageInfo;

/**
 *
 * 文件 服务接口
 * @author CJH
 * on 2019/3/22
 */

public interface IAttachService {

    /**
     * 获取文件信息
     * @param page
     * @param limit
     * @return
     */
    PageInfo<TAttach> getAttachs(int page, int limit);

    /**
     * 保存文件信息
     * @param fname  原文件名
     * @param fkey 文件在服务器中的位置
     * @param ftype 文件类型
     * @param uid 作者名
     */
    void save(String fname, String fkey, String ftype, Integer uid);

    /**
     * 查找
     * @param id
     * @return
     */
    TAttach getAttachById(Integer id);

    /**
     * 删除
     * @param id
     */
    void deleteById(Integer id);
}
