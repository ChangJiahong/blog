package com.cjh.blog;

import com.cjh.blog.utils.TaleUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
//扫描 mybatis mapper 包路径
@MapperScan(basePackages = "com.cjh.blog.mapper")
@EnableTransactionManagement
public class BlogApplication {

    @Value("${upFilePath}")
    private void setUpFilePath(String filePath){
        TaleUtils.setUpFilePath(filePath);
    }

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }

}
