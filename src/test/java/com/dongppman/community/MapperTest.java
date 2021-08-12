package com.dongppman.community;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongppman.community.dao.LoginTicketMapper;
import com.dongppman.community.dao.UserMapper;
import com.dongppman.community.entity.DiscussPost;
import com.dongppman.community.entity.LoginTicket;
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
    @Autowired
    LoginTicketMapper loginTicketMapper;

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
    @Test
    public void testLoginTicket(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60*10));
        loginTicketMapper.insert(loginTicket);

    }
    @Test
    public void testSelectTicket(){
        loginTicketMapper.updateStatusByTicket("abc",1);
        QueryWrapper<LoginTicket> wrapper=new QueryWrapper<>();
        wrapper.eq("ticket","abc");
        LoginTicket loginTicket=loginTicketMapper.selectOne(wrapper);
        System.out.println(loginTicket);

    }

}
