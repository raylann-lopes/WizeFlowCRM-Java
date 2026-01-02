package com.wizeflow.crm_backend.infrastructure.repository;

import com.wizeflow.crm_backend.infrastructure.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> searchCompanyBySlug(String slug);

    Company searchCompanyById(Long id);

    @Transactional
    Optional<Company> deleteCompanyBySlug(String slug);
}