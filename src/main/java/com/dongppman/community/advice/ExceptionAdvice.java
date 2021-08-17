package com.dongppman.community.advice;


import com.dongppman.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//只扫描带有Controller注解的
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger LOGGER= LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.error("服务器发生异常"+e.getMessage());
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            LOGGER.error(stackTraceElement.toString());
        }
        //判断请求类型
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer=response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常!"));
        }else {
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}
