package com.wizeflow.crm_backend.controller;

import com.wizeflow.crm_backend.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    public void findCompanyById(@RequestBody CompanyService companyService) {

    }
}
