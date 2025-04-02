package com.couponmoa.backend.domain.subscribe.userstore.entity;


import com.couponmoa.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class UserStoreSubscribe extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_store_subscribe_id")
    private Long id;
}
