package com.dongppman.community.service;


import com.dongppman.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

//赞模块,由于redis语句简单,没有必要从数据层开始
@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;
    //点赞
    public void like(int userId,int entityType,int entityId,int entityUserId)
    {
//        String entityLikeKey= RedisKeyUtil.getEntityLikeKey(entityType,entityId);
//        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if (isMember)
//        {
//            redisTemplate.opsForSet().remove(entityLikeKey,userId);
//        }else {
//            redisTemplate.opsForSet().add(entityLikeKey,userId);
//        }
        //要保持事务特性
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {

                String entityLikeKey =RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey =RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean isMemeber =redisOperations.opsForSet().isMember(entityLikeKey,userId);
                redisOperations.multi();
                if (isMemeber){
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                }else {
                    redisOperations.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                return  redisOperations.exec();
            }
        });
    }

    //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType,int entityId)
    {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体的点赞状态,1是点赞,0是没点
    public int findEntityLikeStatus(int userId,int entityType,int entityId)
    {
        String entityLikeKey =RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId)?1:0;
    }
    //查询用户得到的赞

    public int findUserLikeCount(int userId)
    {
        String userLikeKey =RedisKeyUtil.getUserLikeKey(userId);
        Integer count=(Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count==null?0:count.intValue();

    }
}
