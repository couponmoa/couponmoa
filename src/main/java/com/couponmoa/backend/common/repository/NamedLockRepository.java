package com.couponmoa.backend.common.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class NamedLockRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Boolean acquireLock(String key, int time) {
        String sql = "SELECT GET_LOCK(:key, :time)";
        Map<String, Object> params = Map.of("key", key, "time", time);
        return jdbcTemplate.queryForObject(sql, params, Boolean.class);
    }

    public Boolean releaseLock(String key) {
        String sql = "SELECT RELEASE_LOCK(:key)";
        Map<String, Object> params = Map.of("key", key);
        return jdbcTemplate.queryForObject(sql, params, Boolean.class);
    }
}
