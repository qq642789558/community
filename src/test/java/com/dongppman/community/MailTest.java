package com.dongppman.community;


import com.dongppman.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;

@SpringBootTest
public class MailTest {
    @Autowired
    private MailClient mailClient;
    @Autowired
    private JavaMailSender javaMailSender;
    @Test
    public void sendMail(){
        mailClient.sendMail("qq642789558@163.com","welcome","welcome");

    }
    @Test
    public void sendSimpleMail() {
        // 构建一个邮件对象
        SimpleMailMessage message = new SimpleMailMessage();
        // 设置邮件主题
        message.setSubject("这是一封测试邮件");
        // 设置邮件发送者，这个跟application.yml中设置的要一致
        message.setFrom("642789558@qq.com");
        // 设置邮件接收者，可以有多个接收者，中间用逗号隔开，以下类似
        message.setTo("qq642789558@163.com");
        // 设置邮件发送日期
        message.setSentDate(new Date());
        // 设置邮件的正文
        message.setText("这是测试邮件的正文");
        // 发送邮件
        javaMailSender.send(message);
    }
}
