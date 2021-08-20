package com.dongppman.community;

import com.dongppman.community.dao.DiscussPostMapper;
import com.dongppman.community.dao.elasticsearch.DiscussPostRepository;
import com.dongppman.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Eleasticsearch是一种分布式的,restful风格的搜索引擎
 * 支持各种类型的检索,可以提供实时的搜索服务,搜索速度快
 * 便于水平扩展,每秒可以处理PB级海量数据
 *
 * 术语:
 * 索引database,类型table,文档(一条数据,采用json储存),字段(字段),集群,节点,分片,副本
 *  现状:索引对应表,类型逐渐废弃
 *  集群:服务器群,节点:一台服务器,分片:对索引进一步的划分,提高并发能力,副本:备份
 *
 *  使用准备:
 *  1.安装elasticsearch
 *  2.安装分词插件ik
 *  3.安装postman进行测试
 *  点击.bat进行启动
 *  指令:curl -X GET "localhost:9200/_cat/health?v"查看健康状态
 *  curl -X GET "localhost:9200/_cat/nodes?v"查看
 *  search:localhost:9200/test/_search?q=title:蚌埠
 *   put:localhost:9200/test/_doc/3
 *
 *
 *   复杂搜索:
 *   {
 * 	"query":{
 * 		"multi_match":{
 * 			"query":"河南",
 * 			"fields":["title","content"]
 *                }* 	}
 * }
 *
 * 绿色，已经加入控制暂未提交
 * 红色，未加入版本控制
 * 蓝色，加入，已提交，有改动
 * 白色，加入，已提交，无改动
 * 灰色：版本控制已忽略文件。
 *
 */
@SpringBootTest
public class ElasticsearchTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    //加入数据
    @Test
    public void testInsert(){
        elasticsearchRestTemplate.save(discussPostMapper.selectById(241));
        elasticsearchRestTemplate.save(discussPostMapper.selectById(242));
        elasticsearchRestTemplate.save(discussPostMapper.selectById(243));
    }

    @Test
    public void tsetInsertList(){
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPorts(101,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPorts(102,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPorts(111,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPorts(112,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPorts(132,0,100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPorts(134,0,100));
    }

    @Test
    public void testUpdate(){
        DiscussPost discussPost=discussPostMapper.selectById(231);
        discussPost.setContent("what'up bro?");
        discussPostRepository.save(discussPost);

    }

    @Test
    public void testSearchByRepository(){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
         //排序,首先按照type的顺序,然后是score,然后是createTime
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                //第一页开始,每页显示10条
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        //指定高亮字段
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);

        // 得到查询结果返回的内容
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        // 设置一个需要返回的实体类集合
        List<DiscussPost> discussPosts = new ArrayList<>();
        // 遍历返回的内容进行处理
        for(SearchHit<DiscussPost> searchHit : searchHits){
            // 高亮的内容
            Map<String, List<String>> highLightFields = searchHit.getHighlightFields();
            // 将高亮的内容填充到content中,有就覆盖
            //可能匹配多个,取第一个
            searchHit.getContent().setTitle(highLightFields.get("title") == null ? searchHit.getContent().getTitle() : highLightFields.get("title").get(0));
            searchHit.getContent().setTitle(highLightFields.get("content") == null ? searchHit.getContent().getContent() : highLightFields.get("content").get(0));
            System.out.println(searchHit.getContent());
            // 放到实体类中
            discussPosts.add(searchHit.getContent());

        }
//        System.out.println(searchHits);
//        System.out.println(discussPosts);
    }



}
