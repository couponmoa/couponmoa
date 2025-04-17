package com.couponmoa.backend.domain.elasticsearch.repository;

import com.couponmoa.backend.domain.elasticsearch.entity.Search;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface SearchRepository extends ElasticsearchRepository<Search, Long> {

    List<Search> findByNameContaining(String Keyword); //이름 기준으로 부분 검색 지원
}
