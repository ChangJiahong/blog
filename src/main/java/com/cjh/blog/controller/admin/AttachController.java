package com.cjh.blog.controller.admin;

import com.cjh.blog.constant.WebConst;
import com.cjh.blog.controller.BaseController;
import com.cjh.blog.pojo.BO.RestResponse;
import com.cjh.blog.pojo.TAttach;
import com.cjh.blog.pojo.TUsers;
import com.cjh.blog.pojo.dto.LogActions;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.pojo.dto.Types;
import com.cjh.blog.service.IAttachService;
import com.cjh.blog.service.ILogService;
import com.cjh.blog.utils.Commons;
import com.cjh.blog.utils.TaleUtils;
import com.github.pagehelper.PageInfo;
import javafx.scene.control.Tab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 附件管理 文件
 * @author CJH
 * on 2019/3/22
 */

@Controller
@RequestMapping(value = "/admin/attach")
public class AttachController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachController.class);

    public static String FILE_PATH = TaleUtils.getUploadFilePath();

    @Autowired
    private IAttachService attachService;

    @Autowired
    private ILogService logService ;


    @GetMapping(value = {"","/"})
    public String index(HttpServletRequest request,
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "12") int limit){

        PageInfo<TAttach>  attachPages = attachService.getAttachs(page, limit);

        request.setAttribute("attachs", attachPages);
        request.setAttribute(Types.ATTACH_URL.getType(), Commons.site_option(Types.ATTACH_URL.getType(), Commons.site_url()));
        request.setAttribute("max_file_size", WebConst.MAX_FILE_SIZE / 1024);

        return getAdminView("attach");
    }


    /**
     * 上传文件
     * @param request
     * @param files
     * @return
     */
    @PostMapping(value = "/upload")
    @ResponseBody
    public RestResponse upload(HttpServletRequest request, @RequestParam(value = "file") MultipartFile[] files){

        TUsers user = this.user(request);
        Integer uid = user.getUid();
        List<String> errorFiles = new ArrayList<>();


        try{
            for (MultipartFile file : files){
                String fname = file.getOriginalFilename();
                if (file.getSize() <= WebConst.MAX_FILE_SIZE) {
                    // 获取 文件 在服务器中的位置
                    String fkey = TaleUtils.getFileKey(fname);
                    String ftype = TaleUtils.isImage(file.getInputStream()) ? Types.IMAGE.getType() : Types.FILE.getType();
                    File nFile = new File(FILE_PATH + fkey);

                    try {
                        // 复制文件
                        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(nFile));

                    } catch (IOException e){
                        e.printStackTrace();
                    }

                    // 保存文件信息
                    attachService.save(fname, fkey, ftype, uid);
                } else {
                    // 添加上传失败 文件
                    errorFiles.add(fname);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            return RestResponse.fail();
        }

        return RestResponse.ok(errorFiles);
    }


    @RequestMapping(value = "delete")
    @ResponseBody
    public RestResponse delete(@RequestParam Integer id, HttpServletRequest request) {

        try {

            TAttach attach = attachService.getAttachById(id);

            if (attach != null) {

                attachService.deleteById(id);
                File file = new File(FILE_PATH + attach.getFkey());
                file.delete();
                logService.insertLog(LogActions.DEL_ARTICLE.getAction(), attach.getFkey(), request.getRemoteAddr(), this.getUid(request));
            }
        }catch (Exception e){
            String msg = "附件删除失败";
            LOGGER.error(msg, e);
            return RestResponse.fail(msg);
        }
        return RestResponse.ok();

    }
}
