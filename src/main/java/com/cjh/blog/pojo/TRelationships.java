package com.cjh.blog.pojo;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "t_relationships")
public class TRelationships implements Serializable {
    @Id
    private Integer cid;

    @Id
    private Integer mid;

    private static final long serialVersionUID = 1L;

    /**
     * @return cid
     */
    public Integer getCid() {
        return cid;
    }

    /**
     * @param cid
     */
    public void setCid(Integer cid) {
        this.cid = cid;
    }

    /**
     * @return mid
     */
    public Integer getMid() {
        return mid;
    }

    /**
     * @param mid
     */
    public void setMid(Integer mid) {
        this.mid = mid;
    }
}