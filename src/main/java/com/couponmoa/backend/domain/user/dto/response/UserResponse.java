package com.couponmoa.backend.domain.user.dto.response;

import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private final Long id;
    private final String email;
    private final String nickname;
    private final UserRole userRole;
    private final String imageUrl;

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userRole(user.getUserRole())
                .imageUrl(generateImageUrl(user.getImageKey()))
                .build();
    }

    private static String generateImageUrl(String imageKey) {
        return "https://couponmoa-user-profile.s3.ap-northeast-2.amazonaws.com/" + imageKey; // s3
    }
}
