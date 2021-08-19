package com.dongppman.community.util;



public class RedisKeyUtil {
    private static final String SPLIT= ":";
    private static final String PREFIX_ENTITY_LIKE="like:entity";
    private static final String PREFIX_USER_LIKE="like:user";
    private static final String PREFIX_FOLLOWEE="followee";
    private static final String PREFIX_FOLLOWER="follower";
    private static final String PREFIX_KAPTCHA="kaptcha";
    private static final String PREFIX_TICKET="ticket";
    private static final String PREFIX_USER="user";

    //某个实体的赞
    //like: entity:entityType:entityId->set(userId)
    public static String getEntityLikeKey(int entityType,int entityId){
        return  PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    //某个用户的zan
    public static String getUserLikeKey(int userId)
    {
        return PREFIX_USER_LIKE +userId;
    }

    //某个用户关注的实体
    //followee:关注的目标,关注者的id,实体类别:指向实体id和当前时间,当前时间用于排序
    //followee:userId:entityType->zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType)
    {
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }
    //某个用户拥有的粉丝
    //follower:entityType:entityId ->zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId)
    {
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+SPLIT+entityId;
    }

    //登陆验证码,一个字符串来标识用户,不同用户验证码应该不同
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }

    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }

    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }
}
