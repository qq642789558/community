package com.dongppman.community.service;

import com.dongppman.community.entity.User;
import com.dongppman.community.util.CommunityConstant;
import com.dongppman.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {
    /**
     * 添加和删除都是操纵数据两次,一次是修改关注列表,一次修改被谁关注的列表
     */
    @Autowired
    private  UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    public void follow(int userId,int entityType,int entityId)
    {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
                redisOperations.multi();
                redisOperations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                return redisOperations.exec();
            }
        });
    }

    public void unfollow(int userId,int entityType,int entityId)
    {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String followeeKey= RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
                redisOperations.multi();
                redisOperations.opsForZSet().remove(followeeKey,entityId);
                redisOperations.opsForZSet().remove(followerKey,userId);
                return redisOperations.exec();
            }
        });
    }

    //查询关注的实体的数量
    public long findFolloweeCount(int userId,int entityType){
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }
    //查询实体的粉丝的数量
    public long findFollowerCount(int entityType,int entityId){
        String followerKey=RedisKeyUtil.getFollowerKey(entityType,entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }
    //是否已关注
    public  boolean hasFollowed(int userId,int entityType,int entityId)
    {
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return  redisTemplate.opsForZSet().score(followeeKey,entityId)!=null;
    }

    //查询用户关注的人
    public List<Map<String,Object>> findFollowees( int userId,int offset,int limit){
        String followeeKey=RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        //有序集合,要求从大到小,offset是包含的,所以减一
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if (targetIds==null){
            return null;
        }
        List<Map<String,Object>> list=new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String,Object> map=new HashMap<>();
            User user=userService.findUserById(targetId);
            map.put("user",user);
            //取分数,由毫秒数转化而来
            Double score = redisTemplate.opsForZSet().score(followeeKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
    //查询某个人的粉丝
    public List<Map<String,Object>> findFollowers( int userId,int offset,int limit){
        String followerKey=RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        //返回的是set,redis内置的一个set,是返回了一套有序的
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if(targetIds==null){
            return null;
        }
        List<Map<String,Object>> list=new ArrayList<>();
        for (Integer targetId : targetIds) {
            Map<String,Object> map=new HashMap<>();
            User user=userService.findUserById(targetId);
            map.put("user",user);
            //取分数,由毫秒数转化而来
            Double score = redisTemplate.opsForZSet().score(followerKey, targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}

