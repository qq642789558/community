package com.dongppman.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @TableName discuss_post
 */
@TableName(value ="discuss_post")
@Data
@ToString
public class DiscussPost implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    //0-普通; 1-置顶;
    private Integer type=0;
    //0-正常; 1-精华; 2-拉黑;
    private Integer status=0;
    private Date createTime;
    private Integer commentCount=0;
    private Double score;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;



}