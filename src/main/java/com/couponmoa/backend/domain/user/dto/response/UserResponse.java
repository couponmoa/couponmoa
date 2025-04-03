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

    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .userRole(user.getUserRole())
                .build();
    }
}
