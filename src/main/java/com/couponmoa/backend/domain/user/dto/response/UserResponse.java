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

    public static UserResponse fromEntityV1(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userRole(user.getUserRole())
                .imageUrl(generateS3ImageUrl(user.getImageKey()))
                .build();
    }

    public static UserResponse fromEntityV2(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userRole(user.getUserRole())
                .imageUrl(generateCdnImageUrl(user.getImageKey()))
                .build();
    }

    private static String generateS3ImageUrl(String imageKey) {
        return "https://couponmoa-user-profile.s3.ap-northeast-2.amazonaws.com/" + imageKey; // s3
    }

    private static String generateCdnImageUrl(String imageKey) {
        return "https://d2mm3xa8sjonwp.cloudfront.net/" + imageKey; // cloudfront
    }
}
