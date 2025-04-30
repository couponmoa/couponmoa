package com.couponmoa.backend.domain.coupon.service.v1;

import com.couponmoa.backend.domain.coupon.entity.Search;
import com.couponmoa.backend.domain.coupon.entity.SearchHistory;
import com.couponmoa.backend.domain.coupon.repository.SearchHistoryRepository;
import com.couponmoa.backend.domain.coupon.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponElasticsearchService {

    private final SearchRepository searchRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final RestHighLevelClient elasticsearchClient;

    public List<Search> recommendCoupons(String keyword, String userId) {
        SearchHistory history = SearchHistory.builder()
                .id(UUID.randomUUID().toString())
                .keyword(keyword)
                .userId(userId)
                .timestamp(LocalDateTime.now().toString())
                .build();
        searchHistoryRepository.save(history);

        List<Search> coupons = searchRepository.findByNameContaining(keyword);
        log.info("Elasticsearch 검색 결과 {}건", coupons.size());
        return coupons;
    }

    public List<String> getPopularKeywords(int limit) throws IOException {
        SearchRequest searchRequest = new SearchRequest("search_history");
        searchRequest.source()
                .query(QueryBuilders.matchAllQuery())
                .aggregation(AggregationBuilders.terms("by_keyword")
                        .field("keyword.keyword")
                        .size(limit));

        SearchResponse response = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        Terms terms = response.getAggregations().get("by_keyword");

        return terms.getBuckets().stream()
                .map(Terms.Bucket::getKeyAsString)
                .toList();
    }

    public List<Search> getAllCoupons() {
        return StreamSupport.stream(searchRepository.findAll().spliterator(), false)
                .toList();
    }
}