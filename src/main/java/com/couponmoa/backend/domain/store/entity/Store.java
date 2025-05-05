package com.couponmoa.backend.domain.store.entity;

import com.couponmoa.backend.common.entity.BaseEntity;
import com.couponmoa.backend.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "stores")
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    private String description;

    private String address;


    @Builder
    public Store(User user, String name, String description, String address) {
        this.user = user;
        this.name = name;
        this.description = description;
        this.address = address;
    }

    // update 메서드
    public void update(String name, String description, String address) {
        this.name = name;
        this.description = description;
        this.address = address;
    }

    public void delete() {
        this.setDeletedAt(LocalDateTime.now());
    }
}
