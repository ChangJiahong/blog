package com.cjh.blog.service;

import com.cjh.blog.pojo.TLogs;

import java.util.List;

/**
 * @author CJH
 * on 2019/3/14
 */

public interface ILogService {

    /**
     * 保存操作日志
     *
     * @param logVo
     */
    void insertLog(TLogs logVo);

    /**
     *  保存
     * @param action
     * @param data
     * @param ip
     * @param authorId
     */
    void insertLog(String action, String data, String ip, Integer authorId);

    /**
     * 获取日志分页
     * @param page 当前页
     * @param limit 每页条数
     * @return 日志
     */
    List<TLogs  > getLogs(int page, int limit);
}
