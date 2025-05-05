package com.couponmoa.backend.domain.coupon.repository;

import com.couponmoa.backend.domain.coupon.entity.SearchHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SearchHistoryRepository extends ElasticsearchRepository<SearchHistory, String> {
}