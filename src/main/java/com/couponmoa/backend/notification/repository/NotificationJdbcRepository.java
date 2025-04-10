package com.couponmoa.backend.notification.repository;

import com.couponmoa.backend.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NotificationJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void updateIsNotified(List<Notification> notiList) {
        List<Long> ids = notiList.stream().map(Notification::getId).toList();

        int batchSize = 1000;

        // 1000개 이상을 한 번에 보낼 경우를 대비해 batch size로 나눠서 전송
        for (int i = 0; i < ids.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, ids.size());
            List<Long> batchIds = ids.subList(i, endIndex);

            String sql = String.format(
                    "UPDATE notifications SET is_notified = true WHERE id IN (%s)",
                    batchIds.stream()
                            .map(id -> "?")
                            .collect(Collectors.joining(", "))
            );
            jdbcTemplate.update(sql, batchIds.toArray());
        }
    }
}
