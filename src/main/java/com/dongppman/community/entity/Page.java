package com.dongppman.community.entity;

//封装分页相关数据

import org.springframework.context.annotation.Bean;
import org.springframework.data.relational.core.sql.From;

public class Page {

    //当前页
    private int current =1;
    //一页的数量
    private int limit=10;
    //数据总数
    private int rows;
    //查询路径,复用分页链接
    private String Path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current>=1){
        this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    /**
     * 计算当前页面的起始行
     * @return
     */
    public int getOffset(){
        return (current-1)*limit;
    }

    /**
     * 获取总页数
     */
    public int getTotal(){
        if(rows%limit==0){
            return rows/limit;
        }
        else {
            return rows/limit+1;
        }
    }
    /**
     * 获取起始页码
     */
    public int getFrom(){
        int from=current-2;
        return from <1 ? 1 :from;
    }
    public int getTo(){
        int to =current+2;
        int total=getTotal();
        return to> total ? total:to;
    }

    public Page() {
    }

    public Page(int current, int limit, int rows, String path) {
        this.current = current;
        this.limit = limit;
        this.rows = rows;
        Path = path;
    }
}
