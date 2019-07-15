package com.cjh.blog.service.impl;

import com.cjh.blog.exce.MyException;
import com.cjh.blog.mapper.TRelationshipsMapper;
import com.cjh.blog.pojo.TRelationships;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.service.IRelationshipService;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author CJH
 * on 2019/3/15
 */

@Service
public class RelationshipServiceImpl implements IRelationshipService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RelationshipServiceImpl.class);

    @Autowired
    private TRelationshipsMapper relationshipsMapper;

    /**
     * @param cid
     * @param mid
     * @return
     */
    @Override
    public long countById(Integer cid, Integer mid) {
        LOGGER.debug("Enter countById method:cid = {},mid = {}",cid,mid);

        if (cid != null && mid != null) {
            TRelationships relationship = new TRelationships();
            relationship.setCid(cid);
            relationship.setMid(mid);
            long count = relationshipsMapper.selectCount(relationship);
            LOGGER.debug("Exit countById method return count={}",count);
            return count;
        }
        LOGGER.debug("Exit countById method , cid or mid is null");

        return 0;
    }

    /**
     * 插入
     *
     * @param relationship
     */
    @Transactional
    @Override
    public void insert(TRelationships relationship) {
        relationshipsMapper.insert(relationship);
    }

    /**
     * 删除
     *
     * @param cid
     * @param mid
     */
    @Transactional
    @Override
    public void deleteById(Integer cid, Integer mid) {
        if (cid != null || mid != null) {
            TRelationships relationships = new TRelationships();
            relationships.setCid(cid);
            relationships.setMid(mid);
            relationshipsMapper.delete(relationships);
        }

    }

    /**
     * by id
     *
     * @param cid 文章 id
     * @param mid
     * @return
     */
    @Override
    public List<TRelationships> getRelationshipById(Integer cid, Integer mid) {
        Example example = new Example(TRelationships.class);
        Example.Criteria criteria = example.createCriteria();
        if (cid != null) {
            criteria.andEqualTo(Table.Ralationship.cid.name(), cid);
        }
        if (mid != null) {
            criteria.andEqualTo(Table.Ralationship.mid.name(), mid);
        }

        return relationshipsMapper.selectByExample(example);

    }
}
