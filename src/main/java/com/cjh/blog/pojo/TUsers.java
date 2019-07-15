package com.cjh.blog.pojo;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "t_users")
public class TUsers implements Serializable {
    @Id
    private Integer uid;

    private String username;

    private String password;

    private String email;

    @Column(name = "home_url")
    private String homeUrl;

    @Column(name = "screen_name")
    private String screenName;

    private Integer created;

    private Integer activated;

    private Integer logged;

    @Column(name = "group_name")
    private String groupName;

    private Boolean disable;

    private static final long serialVersionUID = 1L;

    /**
     * @return uid
     */
    public Integer getUid() {
        return uid;
    }

    /**
     * @param uid
     */
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     */
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     */
    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    /**
     * @return home_url
     */
    public String getHomeUrl() {
        return homeUrl;
    }

    /**
     * @param homeUrl
     */
    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl == null ? null : homeUrl.trim();
    }

    /**
     * @return screen_name
     */
    public String getScreenName() {
        return screenName;
    }

    /**
     * @param screenName
     */
    public void setScreenName(String screenName) {
        this.screenName = screenName == null ? null : screenName.trim();
    }

    /**
     * @return created
     */
    public Integer getCreated() {
        return created;
    }

    /**
     * @param created
     */
    public void setCreated(Integer created) {
        this.created = created;
    }

    /**
     * @return activated
     */
    public Integer getActivated() {
        return activated;
    }

    /**
     * @param activated
     */
    public void setActivated(Integer activated) {
        this.activated = activated;
    }

    /**
     * @return logged
     */
    public Integer getLogged() {
        return logged;
    }

    /**
     * @param logged
     */
    public void setLogged(Integer logged) {
        this.logged = logged;
    }

    /**
     * @return group_name
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }
}