package com.dongppman.community.controller.interceptor;


import com.dongppman.community.entity.LoginTicket;
import com.dongppman.community.entity.User;
import com.dongppman.community.service.UserService;
import com.dongppman.community.util.CookieUtil;
import com.dongppman.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor  implements HandlerInterceptor {

    @Autowired
    UserService userService;
    //在controller之前执行
    @Autowired
    HostHolder hostHolder;


//     * 拦截器应用:
//     * 1.在请求开始查询登录用户
//     * 2.在本次请求持有用户数据
//     * 3.在模板视图上显示用户数据
//     * 4.在请求结束时清理用户数据

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");
        if(ticket!=null)
        {
            //查询凭证
            LoginTicket  loginTicket= userService.findLoginTicket(ticket);
            //查询凭证是否有效
            if(loginTicket!=null&&loginTicket.getStatus()==0&&loginTicket.getExpired().after(new Date()))
            {
                User user=userService.findUserById(loginTicket.getUserId());
            //在本次请求中持有用户(这是一个并发的过程,所以要考虑线程的隔离,调用threadlocal)
                hostHolder.setUsers(user);
            }
        }

        return true;
    }

    //在模板之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
      User user=hostHolder.getUser();
      if(user!=null &&modelAndView!=null)
      {
          modelAndView.addObject("loginUser",user);
      }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
