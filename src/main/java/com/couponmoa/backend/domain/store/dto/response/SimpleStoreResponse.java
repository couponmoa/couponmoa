package com.couponmoa.backend.domain.store.dto.response;

import lombok.Getter;

//이름 + ID만 반환 하기 위해 필요한 Dto
@Getter
public class SimpleStoreResponse {
    private Long id;
    private String name;

    public SimpleStoreResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
