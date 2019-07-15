package com.cjh.blog.mapper;

import com.cjh.blog.pojo.TMetas;
import com.cjh.blog.pojo.dto.Meta;
import com.cjh.blog.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TMetasMapper extends MyMapper<TMetas> {

    List<Meta> selectFromSql(Map<String,Object> paraMap);

    Meta selectByNameAndType(@Param("name") String name, @Param("type") String type);

    Integer countWithSql(Integer mid);
}