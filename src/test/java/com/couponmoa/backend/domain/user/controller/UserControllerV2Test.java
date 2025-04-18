package com.couponmoa.backend.domain.user.controller;

import com.couponmoa.backend.common.service.RedisService;
import com.couponmoa.backend.config.JwtAuthenticationToken;
import com.couponmoa.backend.config.JwtUtil;
import com.couponmoa.backend.config.SecurityConfig;
import com.couponmoa.backend.domain.user.dto.AuthUser;
import com.couponmoa.backend.domain.user.dto.response.UserResponse;
import com.couponmoa.backend.domain.user.entity.User;
import com.couponmoa.backend.domain.user.enums.UserRole;
import com.couponmoa.backend.domain.user.service.UserServiceV2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserControllerV2.class)
@Import({SecurityConfig.class, JwtUtil.class})
public class UserControllerV2Test {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RedisService redisService;

    @MockitoBean
    private UserServiceV2 userServiceV2;

    private JwtAuthenticationToken userAuthenticationToken;

    @BeforeEach
    public void setUp() {
        AuthUser normalUser = new AuthUser(1L, "user@example.com", UserRole.ROLE_USER);
        userAuthenticationToken = new JwtAuthenticationToken(normalUser);
    }

    @Test
    void 사용자_조회() throws Exception {
        //given
        long userId = 1L;
        String email = "email@email.com";
        User user = new User(email, "password", "name", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user, "id", userId);
        UserResponse mockResponse = UserResponse.fromEntityV2(user);
        given(userServiceV2.findUser(anyLong())).willReturn(mockResponse);

        //when&then
        mockMvc.perform(get("/api/v2/users")
                        .with(authentication(userAuthenticationToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userId))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.imageUrl").value(startsWith("https://d2mm3xa8sjonwp.cloudfront.net")));
    }
}
