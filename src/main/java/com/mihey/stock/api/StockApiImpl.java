package com.mihey.stock.api;

import com.mihey.stock.model.Company;
import com.mihey.stock.model.CompanyData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class StockApiImpl implements StockApi {

    private WebClient webClient;

    @Value("${web.url}")
    private String url;
    @Value("${web.dataurl}")
    private String dataUrl;
    @Value("${web.token}")
    private String token;

    public StockApiImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public List<Company> getStockInfo() {
        return webClient
                .get()
                .uri(url + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Company>>() {
                }).block();
    }

    @Override
    public CompanyData getDataByCompany(String companyName) {
        return webClient
                .get()
                .uri(dataUrl + companyName + "/quote?token=" + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CompanyData>() {
                }).block();
    }
}
