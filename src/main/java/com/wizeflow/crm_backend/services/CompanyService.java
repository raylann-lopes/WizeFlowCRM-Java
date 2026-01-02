package com.wizeflow.crm_backend.services;

import com.wizeflow.crm_backend.infrastructure.entity.Company;
import com.wizeflow.crm_backend.infrastructure.repository.CompanyRepository;
import org.springframework.stereotype.Service;

@Service

public class CompanyService {
    private final CompanyRepository repository;

    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    public void SaveCompany(Company company) {
        repository.saveAndFlush(company);
    }

    public Company searchCompanyById(Long id) {
        return repository.searchCompanyById(id);
    }

    public Company searchCompanyBySlug(String slug) {
        return repository.searchCompanyBySlug(slug).orElseThrow(
                () -> new RuntimeException("Empresa nao encontrada")
        );
    }

    public void deleteCompanyBySlug(String slug) {
        repository.deleteCompanyBySlug(slug).orElseThrow(
                () -> new RuntimeException("Empresa inexistente")
        );
    }

    public Company updateCompanyBySlug(String slug, Company company) {
        Company companyEntity = searchCompanyBySlug(slug);
        Company companyUpdate = Company.builder()
                .id(companyEntity.getId())
                .slug(slug)
                .name(company.getName() != null ? company.getName() : companyEntity.getName())
                .phone(company.getPhone() != null ? company.getPhone() : companyEntity.getPhone())
                .settings(company.getSettings() != null ? company.getSettings() : companyEntity.getSettings())
                .plan(company.getPlan() != null ? company.getPlan() : companyEntity.getPlan())
                .status(company.getStatus() != null ? company.getStatus() : companyEntity.getStatus())
                .updatedAt(companyEntity.getUpdatedAt())
                .createdAt(companyEntity.getCreatedAt())
                .users(companyEntity.getUsers())
                .clients(companyEntity.getClients())
                .build();
        SaveCompany(companyUpdate);
        return companyUpdate;

    }
}
