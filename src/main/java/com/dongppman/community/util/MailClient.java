package com.dongppman.community.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {
    /**
     * 记录日志
     */
    private  static final Logger logger= LoggerFactory.getLogger(MailClient.class);

    /**
     * JavaMailSender 也是由spring进行管理,所以不用注册,可以直接注入
     */
    @Autowired
    private JavaMailSender mailSender;
    /**
        调用配置文件中属性的格式:${}
     */
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 通过实现MimeMessage来实现发邮件的操作,而有一个MimeMessageHelper 来帮助实现message
     *
     * @param to
     */
    public void sendMail(String to,String subject,String content){
        try {
            MimeMessage message=mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            System.out.println(from);
            helper.setTo(to);
            System.out.println(to);
            helper.setSubject(subject);
            //允许发送html格式邮件
            helper.setText(content,true);
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送失败"+e.getMessage());
        }
    }
}
