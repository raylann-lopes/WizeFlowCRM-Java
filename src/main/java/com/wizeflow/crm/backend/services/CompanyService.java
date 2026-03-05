package com.wizeflow.crm.backend.services;

import com.wizeflow.crm.backend.infrastructure.entity.Company;
import com.wizeflow.crm.backend.infrastructure.repository.CompanyRepository;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {
    private final CompanyRepository repository;

    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    public void saveCompany(Company company) {
        repository.saveAndFlush(company);
    }

    public Company findCompanyById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new RuntimeException("Empresa nao encontrada")
        );
    }

    public Company findCompanyBySlug(String slug) {
        return repository.findBySlug(slug).orElseThrow(
                () -> new RuntimeException("Empresa nao encontrada")
        );
    }

    public void deleteCompanyBySlug(String slug) {
        repository.deleteBySlug(slug);
    }

    public Company updateCompanyBySlug(String slug, Company company) {
        Company companyEntity = findCompanyBySlug(slug);
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
        saveCompany(companyUpdate);
        return companyUpdate;
    }
}

