package com.mihey.stock.service.impl;

import com.mihey.stock.model.CompanyData;
import com.mihey.stock.repository.CompanyDataRepository;
import com.mihey.stock.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyDataRepository companyDataRepository;

    @Autowired
    public CompanyServiceImpl(CompanyDataRepository companyDataRepository) {
        this.companyDataRepository = companyDataRepository;
    }

    @Override
    public List<CompanyData> saveAll(List<CompanyData> data) {
        return companyDataRepository.saveAll(data);
    }

    @Override
    public List<CompanyData> insert(List<CompanyData> data) {
        return companyDataRepository.insert(data);
    }
}
