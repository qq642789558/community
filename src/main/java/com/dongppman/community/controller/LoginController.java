package com.dongppman.community.controller;

import com.dongppman.community.entity.User;
import com.dongppman.community.service.UserService;
import com.dongppman.community.util.CommunityConstant;
import com.google.code.kaptcha.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    Producer producer;

    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String RegisterPage(){
        return "/site/register";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String LoginPage(){
        return "/site/login";
    }

    //只要属性名和值相匹配
    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(Model model, User user)
    {
        Map<String,Object>map = userService.register(user);
        if(map==null||map.isEmpty()){
            model.addAttribute("msg","注册成功,我们向你发送了一封激活邮件");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        else {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }
    }
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == ACTIVATION_SUCCESS) {
            model.addAttribute("msg","激活成功,账号可以使用");
            model.addAttribute("target","/login");
        } else if (result == ACTIVATION_REPEAT) {
            model.addAttribute("msg","该账号已经激活过!");
            model.addAttribute("msg","/index");
        } else {
            model.addAttribute("msg","验证码错误");
            model.addAttribute("target","/index");
        }
        return  "/site/operate-result";
    }
    //获取图片
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public  void  getKaptcha(HttpServletResponse response, HttpSession session)
    {
        //生成验证码
        String text=producer.createText();
        BufferedImage image=producer.createImage(text);
        //验证码存入session
        session.setAttribute("kaptcha",text);
        //图片输出给浏览器
        response.setContentType("image/png");
        OutputStream outputStream= null;
        //流会由springmvc控制关闭,不用人为操作
        try {
            outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            logger.error("响应验证码失败"+e.getMessage());
        }

    }
}
