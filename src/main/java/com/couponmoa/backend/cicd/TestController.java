package com.couponmoa.backend.cicd;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private static final String version = "v1.0.2";

    @GetMapping("/")
    public String home() {
        return "CICD Docker version: " + version;
    }

}

