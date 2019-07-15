package com.cjh.blog.service;

import com.cjh.blog.exce.MyException;
import com.cjh.blog.pojo.TUsers;

import java.util.List;

/**
 * @author CJH
 * on 2019/3/14
 */

public interface IUserService {


    /**
     * 登录程序
     * @param username
     * @param password
     * @return
     */
    TUsers login(String username, String password);

    /**
     * 获取用户组
     * @return
     */
    List<TUsers> getUsersByGroup(String groupName);

    /**
     * 获取用户
     * @param uid
     * @return
     */
    TUsers getUserById(Integer uid);

    /**
     * 注册用户
     * @param user
     * @return
     */
    TUsers register(TUsers user);

    /**
     * 删除
     * @param uId
     */
    void delById(Integer uId);

    /**
     * 禁用
     * @param uId
     * @param disable
     */
    void disableById(Integer uId, Boolean disable);

    /**
     * 更新用户
     * @param userT
     */
    void updateById(TUsers userT);
}
