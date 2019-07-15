package com.cjh.blog.service.impl;

import com.cjh.blog.exce.MyException;
import com.cjh.blog.mapper.TUsersMapper;
import com.cjh.blog.pojo.TUsers;
import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.service.IUserService;
import com.cjh.blog.utils.DateKit;
import com.cjh.blog.utils.TaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author CJH
 * on 2019/3/14
 */

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private TUsersMapper usersMapper;

    /**
     * 登录程序
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public TUsers login(String username, String password) {

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            throw new MyException("用户名或密码不允许为空");
        }

        if (!userIsExists(username)){
            throw new MyException("不存在该用户");
        }
        String pwd = TaleUtils.MD5encode(username+password);
        Example example = new Example(TUsers.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(Table.Users.username.name(), username);
        criteria.andEqualTo(Table.Users.password.name(), pwd);
        List<TUsers> user = usersMapper.selectByExample(example);
        if (user.size() != 1){
            throw new MyException("密码错误");
        }
        if (!user.get(0).getDisable()){
            throw new MyException("该账号已被封！详情请联系管理员！");
        }

        return user.get(0);
    }

    public boolean userIsExists(String username){
        Example example = new Example(TUsers.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(Table.Users.username.name(), username);
        long count = usersMapper.selectCountByExample(example);
        if (count >= 1){
            return true;
        }
        return false;
    }
    /**
     * 注册用户
     * @return
     */
    @Transactional
    @Override
    public TUsers register(TUsers user){
        if (StringUtils.isBlank(user.getUsername())
                || StringUtils.isBlank(user.getEmail())
                || StringUtils.isBlank(user.getPassword())
                || StringUtils.isBlank(user.getScreenName())
                || StringUtils.isBlank(user.getGroupName())){
            throw new MyException("用户信息不完整");
        }

        if (userIsExists(user.getUsername())){
            throw new MyException("该账户已存在");
        }


        Example example = new Example(TUsers.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(Table.Users.email.name(), user.getEmail());
        long count = usersMapper.selectCountByExample(example);
        if (count >= 1){
            throw new MyException("该邮箱已注册");
        }

//        DateKit.getNowTime()

        user.setPassword(TaleUtils.MD5encode(user.getUsername()+user.getPassword()));
        user.setCreated(DateKit.getCurrentUnixTime());

        int re = usersMapper.insertSelective(user);
        if (re>0){
            return user;
        }

        return null;
    }


    /**
     * 获取用户
     * @param groupName
     * @return
     */
    @Override
    public List<TUsers> getUsersByGroup(String groupName) {
        if (StringUtils.isNoneBlank(groupName)) {
            Example example = new Example(TUsers.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(Table.Users.groupName.name(), groupName);
            return usersMapper.selectByExample(example);
        }
        return null;
    }

    /**
     * 获取用户
     *
     * @param uid
     * @return
     */
    @Override
    public TUsers getUserById(Integer uid) {
        TUsers user = null;

        if (uid != null){
            user = usersMapper.selectByPrimaryKey(uid);
        }

        return user;
    }

    /**
     * 删除
     * @param uId
     * @return
     */
    @Transactional
    @Override
    public void delById(Integer uId) {
        if (uId != null){
            usersMapper.deleteByPrimaryKey(uId);
        }
    }

    /**
     * 禁用
     * @param uId
     * @param disable
     */
    @Transactional
    @Override
    public void disableById(Integer uId, Boolean disable) {
        if (uId != null){
            TUsers user = new TUsers();
            user.setUid(uId);
            user.setDisable(disable);
            usersMapper.updateByPrimaryKeySelective(user);
        }
    }

    /**
     * 更新用户
     *
     * @param userT
     */
    @Transactional
    @Override
    public void updateById(TUsers userT) {
        if (userT == null || userT.getUid() == null){
            throw new MyException("user is null");
        }

        int re =usersMapper.updateByPrimaryKeySelective(userT);

        if (re != 1){
            throw new MyException("update user by uid and return is not one!");
        }

    }
}
