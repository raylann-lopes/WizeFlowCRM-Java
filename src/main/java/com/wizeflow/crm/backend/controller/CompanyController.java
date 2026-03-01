package com.wizeflow.crm.backend.controller;

import com.wizeflow.crm.backend.infrastructure.entity.Company;
import com.wizeflow.crm.backend.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    public void createCompany(@RequestBody Company company) {
        companyService.saveCompany(company);
    }
}
