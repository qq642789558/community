package com.dongppman.community.util;

import lombok.Data;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger LOGGER= LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符
    private static final String REPLACEMENT="***";

    //根节点
    private TrieNode rootNode=new TrieNode();

    @PostConstruct//项目启动后运行,初始化前缀树
    public void init(){
        try (
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
            BufferedReader reader=new BufferedReader(new InputStreamReader(resourceAsStream));
        )
        {
            String keyword;
            while((keyword=reader.readLine())!=null)
            {
                this.addKeyword(keyword);
            }

        }catch (Exception e) {
           LOGGER.error("加载敏感信息失败"+e.getMessage());
        } finally {

        }

    }

    private void addKeyword(String keyword)
    {
        TrieNode tempNode=rootNode;
        for(int i=0;i<keyword.length();i++)
        {
            char c=keyword.charAt(i);
            //判断subNode是否为空,不是空则建立
            TrieNode subNode=tempNode.getSubNode(c);
            if(subNode==null)
            {
                subNode=new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            //指向子节点,进入下一轮循环
            tempNode=subNode;
            //设置结束标识
            if(i==keyword.length()-1)
            {
                tempNode.setKeywordEnd(true);
            }
        }
    }
    /**
    检测并替换含有敏感词的字符串
     *
     */
    public String filter(String text)
    {
        if(StringUtils.isBlank(text))
        {
            return null;
        }
        //指针1
        TrieNode tempNode= rootNode;
        //指针2
        int begin=0;
        //指针3
        int position=0;
        //结果
        StringBuilder sb=new StringBuilder();
        while (position<text.length())
        {
            //要过滤符号如妈·的
            char c=text.charAt(position);
            //添加跳过符号的方法
            if (isSymbol(c))
            {
                //如果非法字符在根节点,则计入结果,指针2前移
                if(tempNode==rootNode)
                {
                    sb.append(c);
                    begin++;
                }
                //不论是否在根节点,指针3都要下移
                position++;
                continue;
            }
            //检查下级节点
            tempNode =tempNode.getSubNode(c);
            if (tempNode==null)
            {
                //不是敏感词
                sb.append(text.charAt(begin));
                //进入下一个位置
                position =++begin;
                //重新指向根节点
                tempNode=rootNode;
            }else if (tempNode.isKeywordEnd())
            {
                //发现敏感字符,判断是否为敏感词结尾,如果是,则替换掉
                sb.append(REPLACEMENT);
                //进入下一个位置
                begin=++position;
                //重新指向root
                tempNode=rootNode;
            }else {
                //发现敏感字符,但是不是敏感词结尾
                position++;
            }
        }
        //将最后一段字符,即指针2未到结尾,指针3到达结尾
        sb.append(text.substring(begin));
        return  sb.toString();
    }
//判断是否为符号
    private boolean isSymbol(Character c)
    {
        //0x2e80~0x9fff是东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2e80||c>0x9fff);
    }



    @Data//前缀树
    private class TrieNode{
        //标志
        private boolean isKeywordEnd=false;
        //子节点,key是下个字符,value是下级节点
        private Map<Character,TrieNode> subNodes=new HashMap<>();
        //添加子节点
        public void addSubNode(Character character, TrieNode node)
        {
            subNodes.put(character, node);
        }
        //获取子节点
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
}
