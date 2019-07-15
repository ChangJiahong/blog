package com.cjh.blog.mapper;

import com.cjh.blog.pojo.BO.Archive;
import com.cjh.blog.pojo.TContents;
import com.cjh.blog.utils.MyMapper;

import java.util.List;

public interface TContentsMapper extends MyMapper<TContents> {
    /**
     * 查找归档记录
     * @return
     */
    List<Archive> findReturnArchive();


    List<TContents> findByCatalog(Integer mid);
}