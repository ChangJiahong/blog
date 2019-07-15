package com.cjh.blog.service.impl;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.mapper.TLogsMapper;
import com.cjh.blog.pojo.TLogs;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.service.ILogService;
import com.cjh.blog.utils.DateKit;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * 日志
 * @author CJH
 * on 2019/3/14
 */

@Service
public class LogServiceImpl implements ILogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogServiceImpl.class);

    @Autowired
    private TLogsMapper logsMapper ;

    /**
     * 保存操作日志
     *
     * @param logVo
     */
    @Override
    public void insertLog(TLogs logVo) {
        logsMapper.insert(logVo);
    }

    /**
     * 保存
     *
     * @param action
     * @param data
     * @param ip
     * @param authorId
     */
    @Transactional
    @Override
    public void insertLog(String action, String data, String ip, Integer authorId) {

        TLogs log = new TLogs();
        log.setAction(action);
        log.setData(data);
        log.setIp(ip);
        log.setAuthorId(authorId);
        log.setCreated(DateKit.getCurrentUnixTime());
        logsMapper.insert(log);
    }

    /**
     * 获取日志分页
     *
     * @param page  当前页
     * @param limit 每页条数
     * @return 日志
     */
    @Override
    public List<TLogs> getLogs(int page, int limit) {

        LOGGER.debug("Enter getLogs method:page={},linit={}",page,limit);

        if (page <= 0){
            page = 1;
        }
        if (limit < 1 || limit > WebConst.MAX_POSTS){
            limit = 10;
        }
        Example example = new Example(TLogs.class);
        example.orderBy(Table.Logs.id.name()).desc();

        PageHelper.startPage((page - 1) * limit, limit);

        List<TLogs> logs = logsMapper.selectByExample(example);

        LOGGER.debug("Exit getLogs method");
        return logs;
    }
}
