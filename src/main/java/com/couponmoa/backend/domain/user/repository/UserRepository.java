package com.couponmoa.backend.domain.user.repository;

import com.couponmoa.backend.common.repository.BaseRepository;
import com.couponmoa.backend.domain.user.entity.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {

    boolean existsByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmailAndDeletedAtIsNotNull(String email);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByEmail(String email);
}