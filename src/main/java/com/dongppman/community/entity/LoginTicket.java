package com.dongppman.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @TableName login_ticket
 */
@ToString
@AllArgsConstructor
@NoArgsConstructor
@TableName(value ="login_ticket")
@Data
public class LoginTicket implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private Integer userId;

    /**
     * 
     */
    private String ticket;

    /**
     * 0-有效; 1-无效;
     */
    private Integer status;

    /**
     * 
     */
    private Date expired;

}