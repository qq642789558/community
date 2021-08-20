package com.dongppman.community.dao.elasticsearch;


import com.dongppman.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository  extends ElasticsearchRepository<DiscussPost,Integer> {

}
