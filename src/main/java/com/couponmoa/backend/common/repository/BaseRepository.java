package com.couponmoa.backend.common.repository;


import com.couponmoa.backend.common.exception.ApplicationException;
import com.couponmoa.backend.common.exception.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {

    default T findByIdOrElseThrow(ID id, ErrorCode errorCode) {
        return findById(id).orElseThrow(
            () -> new ApplicationException(errorCode, errorCode.getMessage() + " id = " + id)
        );
    }

}
