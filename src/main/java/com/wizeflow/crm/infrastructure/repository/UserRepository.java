package com.wizeflow.crm_backend.infrastructure.repository;

import com.wizeflow.crm_backend.infrastructure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional <User> findByEmail(String email);

    @Transactional
    void deleteByEmail(String email);


}
