package com.dongppman.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/alpha")
public class Alpha {

    /**
     * Http是无状态,有会话的,
     * Cookie 是服务器发送到浏览器,并保存到浏览器端的一小块数据,浏览器下次访问服务器时,会自动携带这部分数据,并发送到服务器
     * Session 是JavaEE的标准,用于在服务端记录客户端信息,数据保存到服务端,更加安全,但是会增加服务端的内存压力
     * Session为了和浏览器对应,它会把自己的sessionID用cookie的形式发送给浏览器
     *
     * 分布式部署时session存在的问题:
     * 不同的服务器之间的session没有办法共享,浏览器如果访问不同的服务器时,可能没有存储session
     * 解决方法:
     * 1.粘性session
     * 服务器产生session给浏览器后,那个浏览器对应的IP以后访问时,由nginx分给产生session的服务器处理,缺点是不够灵活
     * 2.同步session
     * 一个服务器产生session后所有服务器都存储一遍,缺点是影响性能,服务器之间有了耦合
     * 3.单独服务器记录session
     * 缺点:这个服务器挂了会导致所有服务器瘫痪
     * 4.主流办法:
     * 把客户端身份存到cookie中,如果有敏感信息存到数据库里,缺点:存储的关系型数据库一般保存在硬盘里,读取速度没有内存快,访问量大的时候可能存在瓶颈,所以应该存在redis中
     *
     *
     *
     * @param response
     * @return
     */
    //cookie示例
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response)
    {
        Cookie cookie=new Cookie("code","1234");
        //cookie的生效范围
        cookie.setPath("/community/alpha");
        cookie.setMaxAge(60*10);
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookie()
    {
        return "get cookie";
    }


    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","test");
        return "set session";
    }
    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return  "get session";
    }

}
