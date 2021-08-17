package com.dongppman.community.controller;

import com.dongppman.community.annotation.LoginRequired;
import com.dongppman.community.dao.UserMapper;
import com.dongppman.community.entity.User;
import com.dongppman.community.service.LikeService;
import com.dongppman.community.service.UserService;
import com.dongppman.community.util.CommunityUtil;
import com.dongppman.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private  String contextPath;

    @Autowired
    private UserService userService;


    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @LoginRequired
    @RequestMapping(value = "/setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    /**
     * 上传头像
     */
    @LoginRequired
    @RequestMapping(value = "/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null)
        {
            model.addAttribute("error","您还没有选择头像");
            return "/site/setting";
        }
        //避免文件重复,要随机生成文件名
        String originalFilename = headerImage.getOriginalFilename();
        //得到后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //判断是否为空
        if(StringUtils.isBlank(suffix))
        {
            model.addAttribute("error","文件格式不正确");
            return "/site/setting";
        }
        //生成随机文件名
        String fileName= CommunityUtil.generateUUID()+suffix;
        File data=new File(uploadPath+"/"+fileName);
        try {
            headerImage.transferTo(data);
        } catch (IOException e) {
            logger.error("上传文件失败"+e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常",e);
        }
        //更新头像路径(web访问路径)
        //例: http://localhost:8080/community/user/header/xxx.png
        User user=hostHolder.getUser();
        String headerUrl=domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }
    /**
     * 通过流输出,所以不用返回值
     */
    @RequestMapping(path = "/header/{filename}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename")String fileName, HttpServletResponse response)
    {
        fileName = uploadPath + "/" + fileName;
        String suffix=fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try (
            OutputStream os = response.getOutputStream();
            FileInputStream fis = new FileInputStream(fileName);
        ){ byte [] buffer =new byte[1024];
            int b=0;
            while((b=fis.read(buffer))!=-1)
            {
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败"+e.getMessage());
        }

    }
    /**
     * 更改密码
     */
    @LoginRequired
    @RequestMapping(value = "/newpassword",method = RequestMethod.POST)
    public String register(Model model,String oldPassword,String newPassword,String confirmPassword)
    {
        User user = hostHolder.getUser();
        oldPassword=CommunityUtil.md5(oldPassword+user.getSalt());
        if(!user.getPassword().equals(oldPassword)){
            model.addAttribute("passwordMsg","密码错误!");
            return "/site/setting";
        }
        if(StringUtils.isBlank(newPassword))
        {
            model.addAttribute("newPasswordMsg","密码不能为空!");
            return "/site/setting";
        }
        newPassword=CommunityUtil.md5(newPassword+user.getSalt());
        if(newPassword.equals(oldPassword))
        {
            model.addAttribute("newPasswordMsg","新密码不能和旧密码一样!");
            return "/site/setting";
        }
        confirmPassword=CommunityUtil.md5(confirmPassword+user.getSalt());
        if(!newPassword.equals(confirmPassword))
        {
            model.addAttribute("confirmPasswordMsg","两次密码不一致!");
            return "/site/setting";
        }

        userService.updatePassword(user.getId(),newPassword);
        return "redirect:/index";
    }

    //个人主页
    @RequestMapping(path = "/profile/{userId}",method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId,Model model)
    {
         User user=userService.findUserById(userId);
         if (user==null){
             throw new RuntimeException("该用户不存在");
         }
         model.addAttribute("user",user);
        int userLikeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount",userLikeCount);
        return "/site/profile";
    }
}


