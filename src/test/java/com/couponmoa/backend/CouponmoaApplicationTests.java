package com.couponmoa.backend;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class CouponmoaApplicationTests {

    @MockitoBean
    private AmazonS3 amazonS3;

    @Test
    void contextLoads() {
    }
}
