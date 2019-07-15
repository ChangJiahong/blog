package com.cjh.blog.service.impl;

import com.cjh.blog.mapper.TOptionsMapper;
import com.cjh.blog.pojo.TOptions;
import com.cjh.blog.service.IOptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author CJH
 * on 2019/3/22
 */

@Service
public class OptionServiceImpl implements IOptionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptionServiceImpl.class);

    @Autowired
    private TOptionsMapper optionsMapper;


    /**
     * 获取 配置
     *
     * @return
     */
    @Override
    public List<TOptions> getOptions() {
        return optionsMapper.selectAll();
    }

    @Override
    @Transactional
    public void insertOrUpdateOption(String name, String value) {
        LOGGER.debug("Enter insertOption method:name={},value={}", name, value);
        TOptions optionVo = new TOptions();
        optionVo.setName(name);
        optionVo.setValue(value);
        if (optionsMapper.selectByPrimaryKey(name) == null) {
            optionsMapper.insertSelective(optionVo);
        } else {
            optionsMapper.updateByPrimaryKeySelective(optionVo);
        }
        LOGGER.debug("Exit insertOption method.");
    }

    /**
     * 保存设置
     *
     * @param querys
     */
    @Override
    @Transactional
    public void saveOptions(Map<String, String> querys) {
        if (null != querys && !querys.isEmpty()) {
            querys.forEach(this::insertOrUpdateOption);
        }
    }


    @Override
    public TOptions getOptionByName(String name) {
        return optionsMapper.selectByPrimaryKey(name);
    }
}
