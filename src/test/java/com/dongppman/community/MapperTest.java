package com.dongppman.community;

import com.dongppman.community.dao.UserMapper;
import com.dongppman.community.entity.DiscussPost;
import com.dongppman.community.entity.User;
import com.dongppman.community.service.DiscussPostService;
import com.dongppman.community.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    UserService userService;


    @Test
    public void test1(){
        User user=new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.newcoder.com/101.png");
        user.setCreateTime(new Date());
        int rows=userMapper.insert(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
    @Test
    public void testUpdate(){
        int i = userMapper.updateStatusById(150, 1);
        System.out.println(i);
    }
    @Test
    public void selectPosts(){
//        List<DiscussPost> list = discussPostService.findDiscussPosts(101, 0, 10);
//        System.out.println(list);
//
//        int discussPostRows = discussPostService.findDiscussPostRows(101);
//        System.out.println(discussPostRows);

        User userById = userService.findUserById(101);
        System.out.println(userById.getHeaderUrl());
    }
}
