package com.dongppman.community.config;


import com.dongppman.community.controller.interceptor.LoginRequiredInterceptor;
import com.dongppman.community.controller.interceptor.LoginTicketInterceptor;
import com.dongppman.community.controller.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private MessageInterceptor messageInterceptor;

    /**
     * 用security替代登陆拦截器
     * @param registry
     */
   // @Autowired
    //private LoginRequiredInterceptor loginRequiredInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpg","/**/*.js","/**/*.jpeg");

       // registry.addInterceptor(loginRequiredInterceptor)
         //       .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpg","/**/*.js","/**/*.jpeg");
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.png","/**/*.jpg","/**/*.js","/**/*.jpeg");
    }
}
