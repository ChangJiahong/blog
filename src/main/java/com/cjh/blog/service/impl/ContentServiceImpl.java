package com.cjh.blog.service.impl;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.exce.MoreThanOneException;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.exce.NoSuchFoundException;
import com.cjh.blog.mapper.TContentsMapper;
import com.cjh.blog.mapper.TMetasMapper;
import com.cjh.blog.pojo.TContents;
import com.cjh.blog.pojo.TMetas;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.pojo.dto.Types;
import com.cjh.blog.service.IContentService;
import com.cjh.blog.service.IMetaService;
import com.cjh.blog.service.IRelationshipService;
import com.cjh.blog.utils.DateKit;
import com.cjh.blog.utils.PatternKit;
import com.cjh.blog.utils.TaleUtils;
import com.cjh.blog.utils.Tools;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vdurmont.emoji.EmojiParser;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 *
 * 文章内容
 * @author CJH
 * on 2019/3/13
 */

@Service
public class ContentServiceImpl implements IContentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentServiceImpl.class);

    /**
     * 文章dao
     */
    @Autowired
    private TContentsMapper contentsMapper ;

    /**
     * 项目dao
     */
    @Autowired
    private IMetaService metaService;

    @Autowired
    private TMetasMapper metasMapper;


    @Autowired
    private IRelationshipService relationshipService;

    /**
     * 获取文章列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return
     */
    @Override
    public PageInfo<TContents> getContents(int pageNum, int pageSize) {

        PageHelper.startPage(pageNum,pageSize);

        Example example = new Example(TContents.class);
        // 根据创建时间降序排列
        example.orderBy("created").desc();
        Example.Criteria criteria = example.createCriteria();

        // 类别为文章的
        criteria.andEqualTo("type",Types.ARTICLE.getType());

        // 状态为已发布的
        criteria.andEqualTo("status",Types.PUBLISH.getType());


        List<TContents> data = contentsMapper.selectByExample(example);

        PageInfo<TContents> articles = new PageInfo<>(data);


        return articles;
    }

    /**
     * 根据文章id 或 slug 路径获取文章
     *
     * @param cid
     * @return
     */
    @Override
    public TContents getContentByIdOrSlug(String cid) throws NoSuchFoundException, MoreThanOneException{
        if (StringUtils.isNotBlank(cid)){
            // 如果不为空
            if (Tools.isNumber(cid)){
                TContents content = contentsMapper.selectByPrimaryKey(Integer.valueOf(cid));

                return content;
            }else {
                Example example = new Example(TContents.class);

                Example.Criteria criteria = example.createCriteria();

                criteria.andEqualTo(Table.Contents.slug.toString(),cid);

                List<TContents> data = contentsMapper.selectByExample(example);
                if (data.size() == 0){
                    throw new NoSuchFoundException("No found about'"+cid+"'");
                }
                if (data.size() > 1){
                    throw new MoreThanOneException("query content by id and return is not one");
                }

               return data.get(0);

            }
        }
        return null;
    }

    /**
     * 根据id 更新
     *
     * @param temp
     */
    @Transactional
    @Override
    public void updateContentByCid(TContents temp) {
        if (null != temp && null != temp.getCid()) {
            contentsMapper.updateByPrimaryKeySelective(temp);
        }
    }

    /**
     * 更新例子获取文章
     *
     * @param example
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageInfo<TContents> getContentByExample(Example example, int page, int limit) {

        PageHelper.startPage(page,limit);
        List<TContents> contents = contentsMapper.selectByExample(example);
        return new PageInfo<>(contents);

    }

    /**
     * 发布文章
     *
     * @param content
     * @return
     */
    @Transactional
    @Override
    public String publish(TContents content) {

        // 检查 文章是否规范
        String x = checkContent(content);

        if (x != null) return x;



        // 解析表情字符
        content.setContent(EmojiParser.parseToAliases(content.getContent()));

        int time = DateKit.getCurrentUnixTime();
        // 创建时间
        content.setCreated(time);
        // 修改时间
        content.setModified(time);
        // 访问数
        content.setHits(0);
        // 评论数
        content.setCommentsNum(0);
        // 标签
        String tags = content.getTags();
        // 类别
        String categories = content.getCategories();
        // 插入
        contentsMapper.insertSelective(content);

        // 记住类别 标签
        Integer cid = content.getCid();
        metaService.saveTagsOrCategorys(cid, tags, Types.TAG.getType());
        metaService.saveTagsOrCategorys(cid, categories, Types.CATEGORY.getType());

        return WebConst.SUCCESS_RESULT;
    }

    /**
     * 检查 文章 规范
     * @param content
     * @return
     */
    private String checkContent(TContents content) {
        if (content == null){
            return "文章为空";
        }

        if (StringUtils.isBlank(content.getTitle())){
            return "文章标题不能为空";
        }

        if (StringUtils.isBlank(content.getContent())){
            return "文章内容不能为空";
        }

        int titleL = content.getTitle().length();
        if (titleL > WebConst.MAX_TITLE_COUNT){
            return "文章标题过长";
        }

        int contentL = content.getContent().length();
        if (contentL > WebConst.MAX_TEXT_COUNT){
            return "文章内容过长";
        }

        if (content.getAuthorId() == null){
            return "尚未登录";
        }

        // 自定义路径
        if (StringUtils.isNotBlank(content.getSlug())){
            // 文自定义文章路径不为空
            if (content.getSlug().length() < 5){
                return "路径太短";
            }
            if (PatternKit.isNumber(content.getSlug())){
                return "不能为全数字";
            }

            if (!TaleUtils.isPath(content.getSlug())){
                return "路径不合法";
            }
            // 检查路径是否存在
            Example example = new Example(TContents.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(Table.Contents.type.name(), content.getType())
                    .andEqualTo(Table.Contents.slug.name(), content.getSlug());
            if (content.getCid() != null){
                // cid 不为空 表示为修改
                // 查询路径排除 当前文章
                criteria.andNotEqualTo(Table.Contents.cid.name(), content.getCid());
            }
            int count = contentsMapper.selectCountByExample(example);
            if (count > 0){
                return "路径已存在，重新输入";
            }
        }else{
            content.setSlug(null);
        }

        return null;
    }


    /**
     * 修改文章
     *
     * @param content
     * @return
     */
    @Transactional
    @Override
    public String updateArticle(TContents content) {

        // 检查 文章是否规范
        String x = checkContent(content);

        if (x != null){
            return x;
        }


        int time = DateKit.getCurrentUnixTime();
        // 最后编辑时间
        content.setModified(time);
        Integer cid = content.getCid();
        content.setContent(EmojiParser.parseToAliases(content.getContent()));

        contentsMapper.updateByPrimaryKeySelective(content);

        // 删除 关系
        relationshipService.deleteById(cid,null);

        // 新建 关系 保存 tags
        metaService.saveTagsOrCategorys(cid, content.getTags(), Types.TAG.getType());

        // 保存 分类
        metaService.saveTagsOrCategorys(cid, content.getCategories(), Types.CATEGORY.getType());

        return WebConst.SUCCESS_RESULT;
    }

    /**
     * 删除文章
     *
     * @param cid
     * @return
     */
    @Override
    @Transactional
    public String deleteByCid(Integer cid) {
        if (cid == null){
            return "文章id 是空";
        }

        TContents content = this.getContentByIdOrSlug(cid.toString());
        if (content != null){
            contentsMapper.deleteByPrimaryKey(cid);
            // 删除文章 后 删除关系
            relationshipService.deleteById(cid, null);
            return WebConst.SUCCESS_RESULT;
        }
        return "文章不存在";

    }

    /**
     * 更新文章分类
     *
     * @param name 旧名字
     * @param name1 新名字
     */
    @Override
    public void updateCategory(String name, String name1) {
        if (StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(name1)) {
            TContents newcontent = new TContents();
            newcontent.setCategories(name1);
            Example example = new Example(TContents.class);
            example.createCriteria().andEqualTo(Table.Contents.categories.name(), name);
            contentsMapper.updateByExampleSelective(newcontent, example);
        }
    }

    /**
     * by id 获取文章
     *
     * @param cid
     * @return
     */
    @Override
    public TContents getContentByCid(Integer cid) {

        if (cid != null){
            return contentsMapper.selectByPrimaryKey(cid);
        }
        return null;
    }

    /**
     * 获取文章列表 by  分类id
     *
     * @param mid
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageInfo<TContents> getArticlesByMetaId(Integer mid, int page, int limit) {

        int total = metasMapper.countWithSql(mid);
        PageHelper.startPage(page, limit);
        // 通过分类获取文章
        List<TContents> list = contentsMapper.findByCatalog(mid);
        PageInfo<TContents> paginator = new PageInfo<>(list);
        paginator.setTotal(total);
        return paginator;
    }

    /**
     * 模糊匹配 文章 by 关键字
     *
     * @param keyword
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageInfo<TContents> getArticlesByKeyword(String keyword, int page, int limit) {
        PageHelper.startPage(page, limit);
        Example contentVoExample = new Example(TContents.class);
        Example.Criteria criteria = contentVoExample.createCriteria();
        criteria.andEqualTo(Table.Contents.type.name(),Types.ARTICLE.getType());
        criteria.andEqualTo(Table.Contents.status.name(),Types.PUBLISH.getType());

        criteria.andLike(Table.Contents.title.name(),"%" + keyword + "%");
        contentVoExample.setOrderByClause("created desc");
        List<TContents> contentVos = contentsMapper.selectByExample(contentVoExample);
        return new PageInfo<>(contentVos);
    }
}
