package com.dongppman.community.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
@Aspect//切面组件
public class ServiceLogAspect {

    private final static Logger logger= LoggerFactory.getLogger(ServiceLogAspect.class);

    //* 所有权限
    //后面是包名
    //.*所有的方法
    //后面为所有的参数
    @Pointcut("execution(* com.dongppman.community.service.*.*(..))")
    public void pointcut(){
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
       //
        ServletRequestAttributes attributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //有可能为空?不是通过controller调用的,导致出现空指针异常
        if (attributes==null){
            return;
        }
        HttpServletRequest request=attributes.getRequest();
        String ip=request.getRemoteHost();
        String now=new SimpleDateFormat("yyy-MM-dd HH:mm:ss").format(new Date());
        String target=joinPoint.getSignature().getDeclaringTypeName()+"."+joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s],在[%s],访问了[%s].",ip,now,target));
    }

    @After("pointcut()")
    public void after(){

    }


}
