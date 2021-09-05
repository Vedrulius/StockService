package com.mihey.stock.repository;

import com.mihey.stock.model.CompanyData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompanyDataRepository extends MongoRepository<CompanyData, String> {
}
