package com.dongppman.community.config;


import com.dongppman.community.util.CommunityConstant;
import com.dongppman.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig  extends WebSecurityConfigurerAdapter implements CommunityConstant {

    //重写三个方法


    //1.配置webSecurity web参数的configure,来忽略静态资源
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    //配置授权,判断controller中哪些方法是对应角色可以访问的
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers(
                "/user/setting",
                "/user/upload",
                "/discuss/add",
                "/comment/add/**",
                "/letter/**",
                "/notice/**",
                "/like",
                "/follow",
                "/unfollow"
        ).hasAnyAuthority(
                AUTHORITY_USER, AUTHORITY_MODERATOR, AUTHORITY_ADMIN)
        .antMatchers("/discuss/top","/discuss/wonderful").hasAnyAuthority(AUTHORITY_MODERATOR)
        .antMatchers("/discuss/delete").hasAnyAuthority(AUTHORITY_ADMIN)
                //除了上面请求以外都允许,并且关闭csrf
         .anyRequest().permitAll().and().csrf().disable();

    //权限不够时的处理
    http.exceptionHandling()
            //有不同的请求,同步或者异步,如果是异步会需要返回一个提示,不能简单的回复一个页面
        .authenticationEntryPoint(new AuthenticationEntryPoint() {
            //没有登陆
            @Override
            public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                String xRequestWith = httpServletRequest.getHeader("x-requested-with");
                if ("XMLHttpRequest".equals(xRequestWith))
                {
                    httpServletResponse.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer=httpServletResponse.getWriter();
                    writer.write(CommunityUtil.getJSONString(403,"你还没有登陆"));

                }else {
                    httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/login");
                }
            }
        })
        .accessDeniedHandler(new AccessDeniedHandler() {
            //权限不足
            @Override
            public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                String xRequestWith = httpServletRequest.getHeader("x-requested-with");
                if ("XMLHttpRequest".equals(xRequestWith))
                {
                    httpServletResponse.setContentType("application/plain;charset=utf-8");
                    PrintWriter writer=httpServletResponse.getWriter();
                    writer.write(CommunityUtil.getJSONString(403,"权限不足"));

                }else {
                    httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/denied");
                }
            }
        });

    //security 默认拦截logout请求,所以我们可以自己覆盖掉这个方法,虚构一个拦截路径
        http.logout().logoutUrl("/securitylogout");
    }

    /**
     * csrf:同时打开网站和恶意网站,网站进行提交表单时,有病毒的网站窃取你的cookie并且由那个恶意网站进行提交
     *security的策略:返回时带一个隐藏的token,网站无法仿造token,服务器就能分辨身份
     *
     */
}
