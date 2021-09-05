package com.mihey.stock.model;

import lombok.Data;

@Data
public class Company {

    private String symbol;
    private String exchange;
    private String exchangeSuffix;
    private String exchangeName;
    private String name;
    private String date;
    private String type;
    private String iexId;
    private String region;
    private String currency;
    private Boolean isEnabled;
    private String figi;
    private String cik;
    private String lei;
}
