package com.dongppman.community;

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
 */

public class ElasticsearchTest {
}
