package com.dongppman.community.util;

public interface CommunityConstant {


    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS=0;

    /**
     *
     */
    int ACTIVATION_REPEAT=1;
    /**
     *
     */
    int ACTIVATION_FAILURE=2;
    /**
     * 默认状态下账号登录凭证过期的时间
     */
    int DEFAULT_EXPIRED_SECONDS=3600*12;
    /**
     * 记住状态下的登录凭证过期时间
     */
    int REMEBER_EXPIRED_SECONDS=3600*24*100;
    /**
     * 实体类型
     */
    int ENTITY_TYPE_POST=1;
    /**
     * 实体评论
     */
    int ENTITY_TYPE_COMMENT=2;

    /**
     * 关注人
     */
    int ENTITY_TYPE_USER=3;

    /**
     * 主题:评论
     */
    String TOPIC_COMMENT="comment";
    /**
     * 点赞
     */
    String TOPIC_LIKE="like";
    /**
     * 关注
     */
    String TOPIC_FOLLOW="follow";
    /**
     * 系统用户的id=1
     */
    int SYSTEM_USERID=1;
    /**
     *
     */
    String TOPIC_PUBLISH="publish";

    String TOPIC_DELETE="delete";

    /**
     * 普通用户
     */

    String AUTHORITY_USER="user";

    /**
     * 管理员
     */
    String AUTHORITY_ADMIN="admin";

    /**
     * 版主
     */
    String AUTHORITY_MODERATOR="moderator";
}

