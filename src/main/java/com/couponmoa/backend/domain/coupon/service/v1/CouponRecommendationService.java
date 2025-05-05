package com.couponmoa.backend.domain.coupon.service.v1;

import com.couponmoa.backend.domain.coupon.entity.Search;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponRecommendationService {

    private final CouponElasticsearchService couponElasticsearchService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.api.key}")
    private String googleApiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    public List<Search> getAIRecommendations(String userId) throws IOException {
        List<String> keywords = couponElasticsearchService.getPopularKeywords(5);
        List<Search> allCoupons = couponElasticsearchService.getAllCoupons();

        String prompt = "Based on the keywords " + keywords + ", recommend up to 5 coupon IDs from the following list:\n" +
                allCoupons.stream()
                        .map(c -> String.format("ID: %d, Name: %s, Description: %s, Store: %s",
                                c.getCouponId(), c.getName(), c.getDescription(), c.getStoreName()))
                        .reduce("", (a, b) -> a + b + "\n") +
                "Return a JSON object with a key 'recommended_coupon_ids' containing a list of up to 5 coupon IDs.";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                        Map.of("text", prompt)
                ))
        ));
        requestBody.put("generationConfig", Map.of(
                "response_mime_type", "application/json"
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", googleApiKey);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject(GEMINI_API_URL, request, Map.class);
        List<Long> recommendedIds = extractRecommendedIds(response);

        return allCoupons.stream()
                .filter(c -> recommendedIds.contains(c.getCouponId()))
                .toList();
    }

    private List<Long> extractRecommendedIds(Map<String, Object> response) {
        try {
            List<?> candidates = (List<?>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                log.warn("No candidates found in Gemini response");
                return Collections.emptyList();
            }

            Map<String, Object> candidate = (Map<String, Object>) candidates.get(0);
            Map<String, Object> content = (Map<String, Object>) candidate.get("content");
            if (content == null) {
                log.warn("No content found in Gemini response");
                return Collections.emptyList();
            }

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                log.warn("No parts found in Gemini response");
                return Collections.emptyList();
            }

            String text = (String) parts.get(0).get("text");
            Map<String, List<Long>> parsed = objectMapper.readValue(text, Map.class);
            List<Long> recommendedIds = parsed.get("recommended_coupon_ids");
            return recommendedIds != null ? recommendedIds : Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to parse Gemini response", e);
            return Collections.emptyList();
        }
    }
}