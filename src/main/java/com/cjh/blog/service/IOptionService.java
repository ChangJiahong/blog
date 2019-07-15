package com.cjh.blog.service;

import com.cjh.blog.pojo.TOptions;

import java.util.List;
import java.util.Map;

/**
 * @author CJH
 * on 2019/3/22
 */

public interface IOptionService {

    /**
     * 获取 配置
     * @return
     */
    List<TOptions> getOptions();

    /**
     * 保存设置
     * @param querys
     */
    void saveOptions(Map<String, String> querys);


    /**
     * 新建 或 修改
     * @param name
     * @param value
     */
    void insertOrUpdateOption(String name, String value);

    /**
     * get  by name
     * @param name
     * @return
     */
    TOptions getOptionByName(String name);
}
