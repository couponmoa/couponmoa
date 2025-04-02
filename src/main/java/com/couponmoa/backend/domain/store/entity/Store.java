package com.couponmoa.backend.domain.store.entity;

import com.couponmoa.backend.common.entity.BaseEntity;
import com.couponmoa.backend.domain.store.enums.StoreCategory;
import com.couponmoa.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "stores")
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    private String description;

    private String address;

    @Enumerated(EnumType.STRING)
    private StoreCategory storeCategory;

    public Store(String name, String description, String address, StoreCategory storeCategory) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.storeCategory = storeCategory;
    }

}
