package com.mihey.stock.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
public class CompanyData{
    @Id
    private String id;
    private int avgTotalVolume;
    private String calculationPrice;
    private BigDecimal change;
    private double changePercent;
    private String closeSource;
    private BigDecimal close;
    private long closeTime;
    private String companyName;
    private String currency;
    private BigDecimal delayedPrice;
    private long delayedPriceTime;
    private BigDecimal extendedChange;
    private double extendedChangePercent;
    private BigDecimal extendedPrice;
    private long extendedPriceTime;
    private BigDecimal high;
    private BigDecimal latestPrice;
    private String highSource;
    private long highTime;
    private long volume;
    private long previousVolume;
}
