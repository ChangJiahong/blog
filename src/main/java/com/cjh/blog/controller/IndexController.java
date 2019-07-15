package com.cjh.blog.controller;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.exce.MyException;
import com.cjh.blog.pojo.BO.Archive;
import com.cjh.blog.pojo.BO.RestResponse;
import com.cjh.blog.pojo.TComments;
import com.cjh.blog.pojo.TContents;
import com.cjh.blog.pojo.TMetas;
import com.cjh.blog.pojo.TUsers;
import com.cjh.blog.pojo.dto.Meta;
import com.cjh.blog.pojo.dto.Types;
import com.cjh.blog.service.ICommentService;
import com.cjh.blog.service.IContentService;
import com.cjh.blog.service.IMetaService;
import com.cjh.blog.service.ISiteService;
import com.cjh.blog.utils.*;
import com.github.pagehelper.PageInfo;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * @author CJH
 * on 2019/3/13
 */

@Controller
public class IndexController extends BaseController {


    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);



    @Autowired
    private IContentService contentService ;

    @Autowired
    private ICommentService commentService ;

    @Autowired
    private IMetaService metaService ;

    @Autowired
    private ISiteService siteService ;


    /**
     * 首页
     * @param httpServletRequest
     * @param pageSize 每页的文章数
     * @return
     */
    @GetMapping(value = {"","/"})
    public String index(HttpServletRequest httpServletRequest,@RequestParam(value = "pageSize", defaultValue = "12") int pageSize){

        return index(httpServletRequest,1,pageSize);
    }


    @GetMapping(value = "/user")
    @ResponseBody
    public RestResponse getUser(){
        TUsers user = new TUsers();
        user.setEmail("123@qq.com");
        user.setScreenName("cjcc");
        return RestResponse.ok(user);
    }

    /**
     * 根据页码产生文章 首页
     * @param request
     * @param pageNum 页码
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/page/{pageNum}")
    public String index(HttpServletRequest request, @PathVariable int pageNum,@RequestParam(value = "pageSize",defaultValue = "12") int pageSize){
        // pageNum 不能小于0 大于最大值
        pageNum = pageNum < 0 || pageNum > WebConst.MAX_PAGE ?  1 : pageNum;

        // 获取文章
        PageInfo<TContents> articles = contentService.getContents(pageNum,pageSize);


        request.setAttribute("articles", articles);

        if (pageNum > 1){
            this.title(request,"第"+pageNum+"页");
        }

        return getView("index");
    }


    /**
     * 文章页
     * @param request
     * @param cid
     * @return
     */
    @GetMapping(value = {"/article/{cid}","/article/{cid}.html"})
    public String article(HttpServletRequest request,@PathVariable String cid){

        TContents content = contentService.getContentByIdOrSlug(cid);

        if (content == null || content.getStatus().equals(Types.DRAFT.getType())){
            // 文章为空 或者是草稿
            // 返回404
            return get_404();
        }

        // 设置文章相关
        request.setAttribute("article", content);

        // is_post : 是文章
        request.setAttribute("is_post", true);

        // 解析文章评论
        completeArticleComments(request,content);

        // 检查ip访问频率
        if (!checkHitsFrequency(request,cid)){
            updateArticleHit(content.getCid(),content.getHits());
        }

        return getView("post");
    }


    /**
     * 预览文章
     * @param request
     * @param cid
     * @return
     */
    @GetMapping(value = {"article/{cid}/preview", "article/{cid}/preview.html"})
    public String previewArticle(HttpServletRequest request, @PathVariable String cid){
        TContents content = contentService.getContentByIdOrSlug(cid);
        if (content == null){
            return this.get_404();
        }
        request.setAttribute("article", content);
        request.setAttribute("is_post", true);

        // 获取文章评论
        completeArticleComments(request, content);

        if (!checkHitsFrequency(request, cid)){
            // 更新 浏览量
            updateArticleHit(content.getCid(), content.getHits());
        }
        return getView("post");

    }



    /**
     * 更新访问次数
     * @param cid
     * @param hits
     */
    private void updateArticleHit(Integer cid, Integer hits) {
        String key = "article"+cid;
        String field = "hits";
        // 获取文章访问次数
        Integer his = cache.hget(key, field);
        // 文章访问数初始0
        if (hits == null) {
            hits = 0;
        }
        // 访问+1
        his = his == null ? 1 : his+1;

        // 缓存 >10 更新数据库
        if (his >= WebConst.HIT_EXCEED){
            TContents temp = new TContents();
            temp.setCid(cid);
            temp.setHits(hits+his);
            contentService.updateContentByCid(temp);
            // 清零
            cache.hset(key, field, 1);
        }else{
            // 否则 更新his缓存
            cache.hset(key, field, his);
        }

    }


    /**
     * 获取文章评论
     * @param request
     * @param content
     */
    private void completeArticleComments(HttpServletRequest request, TContents content){
        if (content.getAllowComment()){
            // 允许评论 评论页数
            String cp = request.getParameter("cp");
            int cpSize = 6;
            if (StringUtils.isBlank(cp)){
                cp = "1";
            }
            request.setAttribute("cp", cp);
            PageInfo<TComments> commentsPage = commentService.getCommentsByContentId(content.getCid(),Integer.valueOf(cp),cpSize);
            request.setAttribute("comments", commentsPage);
        }
    }




    /**
     * 注销
     * @param request
     * @param session
     * @param response
     */
    @RequestMapping(value = "/logout")
    public void logout(HttpServletRequest request,
                       HttpSession session,
                       HttpServletResponse response){
        session.removeAttribute(WebConst.LOGIN_SESSION_KEY);
        Cookie cookie = new Cookie( WebConst.USER_IN_COOKIE, "");
        cookie.setValue(null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        try{
            response.sendRedirect(request.getContextPath());
        } catch (IOException e){
            e.printStackTrace();
            LOGGER.error("注销失败", e);
        }
    }


    /**
     * 发布评论
     * @param request
     * @param response
     * @param cid
     * @param coid
     * @param author
     * @param mail
     * @param url
     * @param text
     * @param _csrf_token
     * @return
     */
    @PostMapping(value = "/comment")
    @ResponseBody
    public RestResponse comment(HttpServletRequest request, HttpServletResponse response,
                                @RequestParam Integer cid, @RequestParam Integer coid,
                                @RequestParam String author, @RequestParam String mail,
                                @RequestParam String url, @RequestParam String text, @RequestParam String _csrf_token){

        String ref = request.getHeader("Referer");
        if (StringUtils.isBlank(_csrf_token)){
            return RestResponse.fail("评论错误1");
        }

        // 证明浏览过文章
        String token = cache.hget(Types.CSRF_TOKEN.getType(), _csrf_token);

        if (StringUtils.isBlank(token)) {
            return RestResponse.fail("评论错误2");
        }

        if (cid == null || StringUtils.isBlank(text)){
            return RestResponse.fail("参数缺失");
        }

        if (StringUtils.isNotBlank(author) && author.length() > 50){
            return RestResponse.fail("用户名过长");
        }

        if (StringUtils.isNotBlank(mail) && !PatternKit.isEmail(mail)) {
            return RestResponse.fail("邮箱格式不正确");
        }

        if (StringUtils.isNotBlank(url) && !PatternKit.isURL(url)) {
            return RestResponse.fail("请输入正确的URL格式");
        }

        if (text.length() > 200) {
            return RestResponse.fail("请输入200个字符以内的评论");
        }

        String val = IPKit.getIpAddrByRequest(request) + ":" +cid;
        // 从cache获取ip地址信息
        Integer count = cache.hget(Types.COMMENTS_FREQUENCY.getType(), val);
        // 如果 获取的文章点击频率不为空 且 >0
        if (null != count && count > 0) {
            // 表示最近访问过
            return RestResponse.fail("您发表评论太快了");
        }

        author = TaleUtils.cleanXSS(author);
        text = TaleUtils.cleanXSS(text);

        author = EmojiParser.parseToAliases(author);
        text = EmojiParser.parseToAliases(text);

        TComments comments = new TComments();
        comments.setAuthor(author);
        comments.setCid(cid);
        comments.setIp(request.getRemoteAddr());
        comments.setUrl(url);
        comments.setContent(text);
        comments.setMail(mail);
        comments.setParent(coid);

        try {
            String result = commentService.insertComment(comments);

            // 设置浏览器记住用户名，邮箱 一个星期
            cookie("tale_remember_author", URLEncoder.encode(author, "UTF-8"), 7 * 24 * 60 * 60, response);
            cookie("tale_remember_mail", URLEncoder.encode(mail, "UTF-8"), 7 * 24 * 60 * 60, response);

            if (StringUtils.isNotBlank(url)) {
                cookie("tale_remember_url", URLEncoder.encode(url, "UTF-8"), 7 * 24 * 60 * 60, response);
            }
            // 设置对每个文章1分钟可以评论一次
            cache.hset(Types.COMMENTS_FREQUENCY.getType(), val, 1, 60);
            if (!WebConst.SUCCESS_RESULT.equals(result)) {
                return RestResponse.fail(result);
            }
            return RestResponse.ok();
        } catch (Exception e){
            String msg = "评论发布失败";
            LOGGER.error(msg, e);
            return RestResponse.fail(msg);
        }

    }


    /**
     * 分类页
     *
     * @return
     */
    @GetMapping(value = "/category/{keyword}")
    public String categories(HttpServletRequest request,
                      @PathVariable String keyword,
                      @RequestParam(defaultValue = "12") int limit){
        return this.categories(request, keyword, 1, limit);
    }


    /**
     * 分类页面
     * @param request
     * @param keyword
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value = "/category/{name}/{page}")
    public String categories(HttpServletRequest request, @PathVariable String name,
                             @PathVariable int page,
                             @RequestParam(defaultValue = "12") int limit) {

        page = page <0 || page > WebConst.MAX_PAGE ? 1: page;

        Meta meta = metaService.getMetaByTypeAndName(Types.CATEGORY.getType(), name);

        if (meta == null){
            return get_404();
        }

        PageInfo<TContents> contentsPageInfo = contentService.getArticlesByMetaId(meta.getMid(), page, limit);
        request.setAttribute("articles", contentsPageInfo);
        request.setAttribute("meta", meta);
        request.setAttribute("type", "分类");
        request.setAttribute("keyword", name);

        return this.getView("page-category");
    }

    /**
     * 归档页
     *
     * @return
     */
    @GetMapping(value = "/archives")
    public String archives(HttpServletRequest request) {
        List<Archive> archives = siteService.getArchives();
        request.setAttribute("archives", archives);
        return this.getView("archives");
    }

    /**
     * 友链页
     *
     * @return
     */
    @GetMapping(value = "/links")
    public String links(HttpServletRequest request) {
        List<TMetas> links = metaService.getMetasByType(Types.LINK.getType());
        request.setAttribute("links", links);
        return this.getView("links");
    }

    /**
     * 自定义页面,如关于的页面
     */
    @GetMapping(value = "/{pagename}")
    public String page(@PathVariable String pagename, HttpServletRequest request) {
        try {
            TContents contents = contentService.getContentByIdOrSlug(pagename);

            if (null == contents) {
                return this.get_404();
            }
            if (contents.getAllowComment()) {
                String cp = request.getParameter("cp");
                if (StringUtils.isBlank(cp)) {
                    cp = "1";
                }
                // 获取评论
                PageInfo<TComments> commentsPaginator = commentService.getCommentsByContentId(contents.getCid(), Integer.parseInt(cp), 6);
                request.setAttribute("comments", commentsPaginator);
            }
            request.setAttribute("article", contents);
            if (!checkHitsFrequency(request, String.valueOf(contents.getCid()))) {
                updateArticleHit(contents.getCid(), contents.getHits());
            }
        }catch (MyException e){
            return this.get_404();
        }
        return this.getView("page");
    }

    /**
     * 搜索页
     *
     * @param keyword
     * @return
     */
    @GetMapping(value = "/search/{keyword}")
    public String search(HttpServletRequest request, @PathVariable String keyword, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.search(request, keyword, 1, limit);
    }

    /**
     * 搜索
     * @param request
     * @param keyword
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value = "/search/{keyword}/{page}")
    public String search(HttpServletRequest request, @PathVariable String keyword, @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;

        PageInfo<TContents> articles = contentService.getArticlesByKeyword(keyword, page, limit);
        request.setAttribute("articles", articles);
        request.setAttribute("type", "搜索");
        request.setAttribute("keyword", keyword);
        return this.getView("page-category");
    }

    /**
     * 标签页
     *
     * @param name
     * @return
     */
    @GetMapping(value = "/tag/{name}")
    public String tags(HttpServletRequest request, @PathVariable String name, @RequestParam(value = "limit", defaultValue = "12") int limit) {
        return this.tags(request, name, 1, limit);
    }

    /**
     * 标签分页
     *
     * @param request
     * @param name
     * @param page
     * @param limit
     * @return
     */
    @GetMapping(value = "/tag/{name}/{page}")
    public String tags(HttpServletRequest request, @PathVariable String name, @PathVariable int page, @RequestParam(value = "limit", defaultValue = "12") int limit) {

        page = page < 0 || page > WebConst.MAX_PAGE ? 1 : page;
//        对于空格的特殊处理
        name = name.replaceAll("\\+", " ");
        TMetas metaDto = metaService.getMetaByTypeAndName(Types.TAG.getType(), name);
        if (null == metaDto) {
            return this.get_404();
        }

        PageInfo<TContents> contentsPaginator = contentService.getArticlesByMetaId(metaDto.getMid(), page, limit);
        request.setAttribute("articles", contentsPaginator);
        request.setAttribute("meta", metaDto);
        request.setAttribute("type", "标签");
        request.setAttribute("keyword", name);

        return this.getView("page-category");
    }

    /**
     * 设置cookie
     *
     * @param name
     * @param value
     * @param maxAge
     * @param response
     */
    private void cookie(String name, String value, int maxAge, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(false);
        response.addCookie(cookie);
    }

    /**
     * 检查同一个ip地址是否在2小时内访问同一文章
     *
     * @param request
     * @param cid
     * @return
     */
    private boolean checkHitsFrequency(HttpServletRequest request, String cid) {
        // ip + 文章id
        String ip_c = IPKit.getIpAddrByRequest(request) + ":" + cid;

        // 从cache获取ip地址信息
        Integer count = cache.hget(Types.HITS_FREQUENCY.getType(), ip_c);
        // 如果 获取的文章点击频率不为空 且 >0
        if (null != count && count > 0) {
            // 表示最近访问过
            return true;
        }

        cache.hset(Types.HITS_FREQUENCY.getType(), ip_c, 1, WebConst.HITS_LIMIT_TIME);
        return false;
    }

}
