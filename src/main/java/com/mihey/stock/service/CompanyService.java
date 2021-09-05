package com.mihey.stock.service;

import com.mihey.stock.model.CompanyData;

import java.util.List;

public interface CompanyService {
    List<CompanyData> insert(List<CompanyData> data);
    List<CompanyData> saveAll(List<CompanyData> data);
}
