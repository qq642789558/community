package com.dongppman.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    //生成激活码,随机字符串

    public static String generateUUID(){
        //有可能存在横线,所以通过这个去掉
        return UUID.randomUUID().toString().replaceAll("-","");
    }
    //MD5加密(不可逆,每次结果固定)
    //为了加强,要在密码后加入一串随机字符串
    public static String md5(String key){
        //如果是null,空格,空串,都按照空的记录
        if(StringUtils.isBlank(key)){
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

}
