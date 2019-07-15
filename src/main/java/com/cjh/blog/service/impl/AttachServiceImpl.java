package com.cjh.blog.service.impl;

import com.cjh.blog.mapper.TAttachMapper;
import com.cjh.blog.pojo.TAttach;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.service.IAttachService;
import com.cjh.blog.utils.DateKit;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 *
 * 附件
 * @author CJH
 * on 2019/3/22
 */

@Service
public class AttachServiceImpl implements IAttachService {


    @Autowired
    private TAttachMapper attachMapper ;


    /**
     * 获取文件信息
     *
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageInfo<TAttach> getAttachs(int page, int limit) {

        PageHelper.startPage(page, limit);
        Example example = new Example(TAttach.class);
        example.orderBy(Table.Attach.created.name()).desc();

        List<TAttach> attaches = attachMapper.selectByExample(example);

        return new PageInfo<>(attaches);
    }

    /**
     * 保存文件信息
     *
     * @param fname 原文件名
     * @param fkey  文件在服务器中的位置
     * @param ftype 文件类型
     * @param uid   作者名
     */
    @Override
    @Transactional
    public void save(String fname, String fkey, String ftype, Integer uid) {
        TAttach attach = new TAttach();
        attach.setFname(fname);
        attach.setAuthorId(uid);
        attach.setFkey(fkey);
        attach.setFtype(ftype);
        attach.setCreated(DateKit.getCurrentUnixTime());
        attachMapper.insertSelective(attach);
    }

    /**
     * 查找
     *
     * @param id
     * @return
     */
    @Override
    public TAttach getAttachById(Integer id) {

        if (id != null){
            return attachMapper.selectByPrimaryKey(id);
        }

        return null;
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (id != null){
            attachMapper.deleteByPrimaryKey(id);
        }
    }
}
