package com.wizeflow.crm.backend.infrastructure.repository;

import com.wizeflow.crm.backend.infrastructure.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findBySlug(String slug);

    @Transactional
    void deleteBySlug(String slug);
}

