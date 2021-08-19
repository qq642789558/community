package com.dongppman.community.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dongppman.community.dao.LoginTicketMapper;
import com.dongppman.community.dao.UserMapper;
import com.dongppman.community.entity.LoginTicket;
import com.dongppman.community.entity.User;
import com.dongppman.community.util.CommunityConstant;
import com.dongppman.community.util.CommunityUtil;
import com.dongppman.community.util.MailClient;
import com.dongppman.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    RedisTemplate redisTemplate;

    public User findUserById(int id){
//        return userMapper.selectById(id);
    //先从cache里面找
        //没有再初始化
        User user=getCache(id);
        if (user==null){
            user=initCache(id);
        }
        return user;
    }
    @Autowired
    private MailClient mailClient;
    //注入模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    //注入域名
    @Value("${community.path.domain}")
    private String domain;
    //项目名,应用路径
    @Value("${server.servlet.context-path}")
    private String contextPath;


    public User findUserByName(String name)
    {
        return userMapper.selectByName(name);
    }

    public Map<String,Object> register(User user){
        Map<String,Object>map=new HashMap<>();
        //空值处理
        if(user==null)
        {
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername()))
        {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword()))
        {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //验证账号
        User user1 = userMapper.selectByName(user.getUsername());
        if(user1!=null)
        {
            map.put("usernameMsg","该账号已经存在");
            return map;
        }
        user1=userMapper.selectByEmail(user.getEmail());
        if(user1!=null)
        {
            map.put("emailMsg","邮箱已经被注册");
            return map;
        }
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insert(user);
        //激活邮件,thymeleaf包下的
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        //id在之前没有值,但是由mybatis生成
        String url=domain+contextPath+"/activation"+"/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);
        String content=templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);
        return map;
    }
    public int activation(int userId,String code)
    {
        User user=userMapper.selectById(userId);
        if(user.getStatus()==1)
        {
            return ACTIVATION_REPEAT;
        }
        else if(user.getActivationCode().equals(code)){
            userMapper.updateStatusById(userId,1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        }else {
            return ACTIVATION_FAILURE;
        }
    }


    public Map<String,Object> login(String username,String password,Integer expireSeconds)
    {
        Map<String,Object> map=new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username))
        {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if(StringUtils.isBlank(password))
        {
            map.put("passwordMsg","密码不能为空");
            return map;
        }
        //验证账号状态
        User user=userMapper.selectByName(username);
        if(user==null)
        {
            map.put("usernameMsg","该账号不存在");
            return map;
        }
        if (user.getStatus()==0)
        {
            map.put("usernameMsg","该账号没有激活");
            return map;
        }
        //验证密码
        password=CommunityUtil.md5(password+user.getSalt());
        if(!user.getPassword().equals(password))
        {
            map.put("passwordMsg","账号密码错误");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expireSeconds*1000));
//        loginTicketMapper.insert(loginTicket);
        String redisKey= RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        //redis会把它序列成一个字符串
        redisTemplate.opsForValue().set(redisKey,loginTicket);
        map.put("ticket",loginTicket.getTicket());
        return map;

    }
    public void logout(String ticket)
    {
        //loginTicketMapper.updateStatusByTicket(ticket,1);
        String rediskey=RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket=(LoginTicket) redisTemplate.opsForValue().get(rediskey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(rediskey,loginTicket);
    }
    public  LoginTicket findLoginTicket(String ticket)
    {
//        QueryWrapper<LoginTicket> queryWrapper=new QueryWrapper<>();
//        queryWrapper.eq("ticket",ticket);
//        return loginTicketMapper.selectOne(queryWrapper);
        String rediskey=RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(rediskey);
    }


    //设置头像

    public int updateHeader(int id,String headUrl)
    {
//        return userMapper.updateHeaderUrlById(id, headUrl);
        int rows=userMapper.updateHeaderUrlById(id,headUrl);
        clearCache(id);
        return rows;
    }

    //更改密码
    public void updatePassword(int id,String password)
    {
        User user = userMapper.selectById(id);
        userMapper.updatePasswordById(id,password);
        clearCache(id);
    }
    //从缓存中寻找
    private  User getCache(int userId)
    {
        String redisKey=RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    //找不到则初始化数据
    private User initCache(int userId){
        //数据库中寻找用户
        User user=userMapper.selectById(userId);
        //存入缓存
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }

    //更改数据时清除缓存,原因:如果修改可能存在并发问题,不易修改
    private void clearCache(int userId){
        String redisKey=RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

}
