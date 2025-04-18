package com.couponmoa.backend.domain.user.controller.v1;

import com.amazonaws.services.s3.AmazonS3;
import com.couponmoa.backend.common.service.RedisService;
import com.couponmoa.backend.config.JwtAuthenticationToken;
import com.couponmoa.backend.config.JwtUtil;
import com.couponmoa.backend.config.SecurityConfig;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.couponmoa.backend.domain.user.service.v1.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserProfileController.class)
@Import({SecurityConfig.class, JwtUtil.class})
public class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RedisService redisService;

    @MockitoBean
    private AmazonS3 amazonS3;

    @MockitoBean
    private UserProfileService userProfileService;

    private JwtAuthenticationToken userAuthenticationToken;

    @BeforeEach
    public void setUp() {
        AuthUser normalUser = new AuthUser(1L, "user@example.com", UserRole.ROLE_USER);
        userAuthenticationToken = new JwtAuthenticationToken(normalUser);
    }

    @Test
    void 프로필_사진_등록() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "profile.jpg",
                "image/jpeg",
                "fake image content".getBytes()
        );

        willDoNothing().given(userProfileService).updateUserImage(anyLong(), any(MultipartFile.class));

        mockMvc.perform(multipart("/api/v1/users/image")
                        .file(file)
                        .with(authentication(userAuthenticationToken)))
                .andExpect(status().isOk());
    }

    @Test
    void 프로필_사진_삭제() throws Exception {
        willDoNothing().given(userProfileService).deleteUserImage(anyLong());

        mockMvc.perform(delete("/api/v1/users/image")
                        .with(authentication(userAuthenticationToken)))
                .andExpect(status().isOk());
    }
}
