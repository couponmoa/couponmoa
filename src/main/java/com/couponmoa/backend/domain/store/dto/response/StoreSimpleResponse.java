package com.couponmoa.backend.domain.store.dto.response;

import com.couponmoa.backend.domain.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

//이름 + ID만 반환 하기 위해 필요한 Dto
@Getter
@Builder
public class StoreSimpleResponse {
    private Long id;
    private String name;

    public StoreSimpleResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StoreSimpleResponse toDto(Store store) {
        return new StoreSimpleResponse(
                store.getId(),
                store.getName()
        );
    }
}
