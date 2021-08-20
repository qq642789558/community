package com.dongppman.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;


@MapperScan("com.dongppman.community.Dao")
@SpringBootApplication
public class CommunityApplication {


    //管理bean的生命周期,由注解修饰的方法在构造器调用完以后执行
    @PostConstruct
    public void init(){
        //解决netty启动冲突问题
        //在netty4utils中
        System.setProperty("es.set.netty.runtime.available.processors","false");
    }

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }

}


