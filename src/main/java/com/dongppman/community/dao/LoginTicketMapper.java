package com.dongppman.community.dao;

import com.dongppman.community.entity.LoginTicket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

/**
 * @Entity com.dongppman.community.entity.LoginTicket
 */
@Deprecated
@Component(value = "LoginTicketMapper")
public interface LoginTicketMapper extends BaseMapper<LoginTicket>
{

    @Update({
            "update login_ticket set status=#{status} where ticket=#{ticket}"
    })
    int updateStatusByTicket(String ticket,int status);
}




