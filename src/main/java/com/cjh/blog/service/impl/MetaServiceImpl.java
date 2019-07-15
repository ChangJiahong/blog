package com.cjh.blog.service.impl;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.mapper.TMetasMapper;
import com.cjh.blog.pojo.TContents;
import com.cjh.blog.pojo.TMetas;
import com.cjh.blog.pojo.TRelationships;
import com.cjh.blog.pojo.dto.Meta;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.pojo.dto.Types;
import com.cjh.blog.service.IContentService;
import com.cjh.blog.service.IMetaService;
import com.cjh.blog.service.IRelationshipService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CJH
 * on 2019/3/14
 */

@Service
public class MetaServiceImpl implements IMetaService {

    /**
     * meta dao
     */
    @Autowired
    private TMetasMapper metasMapper;

    @Autowired
    private IContentService contentService;

    @Autowired
    private IRelationshipService relationshipService;


    /**
     * 保存 标签或分类
     * 保存操作 在新建文章时 在修改文章时  用于新建标签 分类等
     * 将metas 分割开 单独保存
     * @param cid
     * @param names
     * @param type
     */
    @Transactional
    @Override
    public void saveTagsOrCategorys(Integer cid, String names, String type) {
        if (cid == null){
            throw new MyException("项目关联id不能为空");
        }
        if (StringUtils.isNotBlank(names) && StringUtils.isNotBlank(type)){
            String[] args = names.split(",");
            for (String name : args){
                this.saveMetaOrUpdate(cid,name ,type );
            }
        }
    }

    /**
     * 根据类型 获取 分类 or 标签 or link
     *
     * @param type
     * @return
     */
    @Override
    public List<TMetas> getMetasByType(String type) {
        if (StringUtils.isNotBlank(type)){
            Example example = new Example(TMetas.class);
            example.orderBy(Table.Meta.sort.name()).desc()
                    .orderBy(Table.Meta.mid.name()).desc();
            example.createCriteria().andEqualTo(Table.Meta.type.name(), type);
            return metasMapper.selectByExample(example);
        }

        return null;
    }


    /**
     * 新建 或 更新
     * 如果已有该类型标签则修改 没有则新建
     * 并更新关系表 如果有关系则不做操作 如果没有则新建关系
     * @param cid
     * @param name
     * @param type
     */
//    @Transactional(rollbackFor = MyException.class)
    private void saveMetaOrUpdate(Integer cid, String name, String type) {
        if (cid == null){
            throw new MyException("项目关联id不能为空");
        }
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)){
            // 查找 是否有重名的meta
            Example example = new Example(TMetas.class);
            example.createCriteria().andEqualTo(Table.Meta.name.name(), name)
                    .andEqualTo(Table.Meta.type.name(), type);
            List<TMetas> metas = metasMapper.selectByExample(example);

            int mid = 0;
            TMetas meta ;
            if (metas.size() == 1){
                // 找到一个 不用新建
                // 直接修改
                meta = metas.get(0);
                mid = meta.getMid();
            } else if (metas.size() > 1){
                throw new MyException("查到多个meta");
            } else {
                // 没有找到
                // 插入
                meta = new TMetas();
                meta.setSlug(name);
                meta.setType(type);
                meta.setName(name);
                metasMapper.insert(meta);
                mid = meta.getMid();
            }
            if (mid != 0){
                // 建立 用户-meta 关系表
                long count = relationshipService.countById(cid, mid);
                if (count == 0){
                    // 新建
                    TRelationships relationship = new TRelationships();
                    relationship.setMid(mid);
                    relationship.setCid(cid);
                    relationshipService.insert(relationship);
                }
            }

        } else {
            throw new MyException("参数不完整");
        }

    }


    /**
     * 新建分类 如果已有mid则修改
     * @param type
     * @param name
     * @param mid
     */
    @Override
    @Transactional
    public void saveCategoryOrUpdateById(String type, String name, Integer mid){

        // type name 不为空
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)) {
            Example example = new Example(TMetas.class);
            // type name 符合
            example.createCriteria().andEqualTo(Table.Meta.type.name(), type)
                    .andEqualTo(Table.Meta.name.name(), name);
            // 判断是否已有 tag
            int cont = metasMapper.selectCountByExample(example);
            TMetas meta = null;
            if (cont != 0){
                throw new MyException("已存在该分类");
            }
            meta = new TMetas();
            meta.setName(name);
            if (mid != null){
                // 已有 修改
                // 查找原先分类
                TMetas original = metasMapper.selectByPrimaryKey(mid);
                meta.setMid(Integer.valueOf(mid));
                metasMapper.updateByPrimaryKeySelective(meta);
//                    更新原有文章的categories
                contentService.updateCategory(original.getName(), name);
            } else {
                // 新建
                meta.setType(type);
                metasMapper.insertSelective(meta);
            }

        }
    }

    /**
     * 获取 分类 列表 每个分类所属文章数
     *  @param type
     * @param orderBy  排序
     * @param maxPosts
     */
    @Override
    public List<Meta> getCategoryList(String type, String orderBy, int maxPosts) {
        if (StringUtils.isNotBlank(type)) {
            if (StringUtils.isBlank(orderBy)) {
                orderBy = "count desc, a.mid desc";
            }
            if (maxPosts < 1 || maxPosts > WebConst.MAX_POSTS) {
                maxPosts = 10;
            }
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("type", type);
            paraMap.put("order", orderBy);
            paraMap.put("limit", maxPosts);
            return metasMapper.selectFromSql(paraMap);
        }
        return null;
    }

    /**
     * delete by mid
     *
     * @param mid
     */
    @Override
    @Transactional
    public void delete(int mid) {
        TMetas meta = metasMapper.selectByPrimaryKey(mid);
        if (meta != null){
            String type = meta.getType();
            String name = meta.getName();

            // 删除标签
            metasMapper.deleteByPrimaryKey(mid);

            // 更新所有文章 的 标签和分类
            List<TRelationships> rlist = relationshipService.getRelationshipById(null, mid);
            if (null != rlist) {
                for (TRelationships r : rlist) {
                    TContents contents = contentService.getContentByIdOrSlug(String.valueOf(r.getCid()));
                    if (null != contents) {
                        TContents temp = new TContents();
                        temp.setCid(r.getCid());

                        if (type.equals(Types.CATEGORY.getType())) {
                            temp.setCategories(reMeta(name, contents.getCategories()));
                        }
                        if (type.equals(Types.TAG.getType())) {
                            temp.setTags(reMeta(name, contents.getTags()));
                        }
                        contentService.updateContentByCid(temp);
                    }
                }
            }
        }
    }

    /**
     * 保存
     *
     * @param link
     */
    @Override
    @Transactional
    public void saveMeta(TMetas link) {
        if (link != null){
            metasMapper.insertSelective(link);
        }
    }

    /**
     * 修改
     * @param link
     */
    @Override
    @Transactional
    public void updateById(TMetas link) {
        if (link != null && link.getMid() != null){
            metasMapper.updateByPrimaryKeySelective(link);
        }
    }

    /**
     * 获取分类信息
     *
     * @param type
     * @param name
     * @return
     */
    @Override
    public Meta getMetaByTypeAndName(String type, String name) {
        if (StringUtils.isNotBlank(type) && StringUtils.isNotBlank(name)){
            return metasMapper.selectByNameAndType(name, type);
        }

        return null;
    }

    /**
     * 移除 标签 或者 分类
     * @param name
     * @param metas
     * @return
     */
    private String reMeta(String name, String metas) {
        String[] ms = StringUtils.split(metas, ",");
        StringBuilder sbuf = new StringBuilder();
        for (String m : ms) {
            if (!name.equals(m)) {
                sbuf.append(",").append(m);
            }
        }
        if (sbuf.length() > 0) {
            return sbuf.substring(1);
        }
        return "";
    }

}
