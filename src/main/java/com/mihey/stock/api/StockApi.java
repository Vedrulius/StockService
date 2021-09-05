package com.mihey.stock.api;

import com.mihey.stock.model.Company;
import com.mihey.stock.model.CompanyData;

import java.util.List;

public interface StockApi {
    List<Company> getStockInfo();
    CompanyData getDataByCompany(String companyName);
}
