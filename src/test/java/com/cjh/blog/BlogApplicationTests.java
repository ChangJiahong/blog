package com.cjh.blog;

import com.cjh.blog.pojo.dto.Table;
import com.cjh.blog.utils.Commons;
import com.cjh.blog.utils.TaleUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogApplicationTests {

    @Test
    public void contextLoads() {

        System.out.println(Commons.gravatar("2327085154@qq.com"));
    }

}
