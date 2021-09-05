package com.mihey.stock.service;

import com.mihey.stock.api.StockApi;
import com.mihey.stock.model.Company;
import com.mihey.stock.model.CompanyData;
import com.mihey.stock.model.CompanyDataVolumeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@EnableCaching
public class StockService {

    private final CompanyService companyService;
    private final StockApi stockApi;
    private List<Company> companies;
    @Resource
    private StockService stockService;

    @Autowired
    public StockService(StockApi stockApi, CompanyService companyService) {
        this.stockApi = stockApi;
        this.companyService = companyService;
    }

    @PostConstruct
    private void setUp() {
        companies = getStockInfo();
    }

    private List<Company> getStockInfo() {
        return stockApi.getStockInfo();
    }

    private CompanyData getDataByCompany(String companyName) {
        return stockApi.getDataByCompany(companyName);
    }

    @Cacheable(value="companydata")
    public List<CompanyData> getAllData() {
        List<Company> enabledCompanies = companies.stream().filter(Company::getIsEnabled).collect(Collectors.toList());
        ExecutorService executor = Executors.newFixedThreadPool(3);
        List<CompanyData> data = new ArrayList<>();
        List<Callable<CompanyData>> callables = new ArrayList<>();
        enabledCompanies.forEach(company -> callables.add(() -> getDataByCompany(company.getSymbol())));
        try {
            List<Future<CompanyData>> futures = executor.invokeAll(callables);
            for (Future<CompanyData> future : futures) {
                data.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        storeData(data);
        executor.shutdownNow();
        return data;
    }

    private void storeData(List<CompanyData> data) {
        companyService.insert(data);
    }

    @Scheduled(fixedDelayString = "${fixedDelay.in.milliseconds}")
    public void runQuery() {
        List<CompanyData> dataFromCache = stockService.getAllData();
        stockService.clearCache();
        Map<String, BigDecimal> changeStockPriceByCompany = new HashMap<>();
            Map<String, BigDecimal> currentLatestPriceByCompany = dataFromCache.stream()
                    .collect(HashMap::new, (m, v) -> m.put(v.getCompanyName(), v.getLatestPrice()), HashMap::putAll);
        List<CompanyData> data = stockService.getAllData();
            Map<String, BigDecimal> newLatestPriceByCompany = data.stream()
                    .collect(HashMap::new, (m, v) -> m.put(v.getCompanyName(), v.getLatestPrice()), HashMap::putAll);
            for (String s : newLatestPriceByCompany.keySet()) {
                changeStockPriceByCompany.put(s, getValueChange(newLatestPriceByCompany.get(s), currentLatestPriceByCompany.get(s)));
            }
        System.out.printf("%-80s %10s\n", "Company name", "Volume");
        getHighestVol(data).forEach(d -> {
            if (d.getVolume() == 0) {
                System.out.printf("%-80s %10d\n", d.getCompanyName(), d.getPreviousVolume());
            } else {
                System.out.printf("%-80s %10d\n", d.getCompanyName(), d.getVolume());
            }
        });
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-80s %10s\n", "Company name", "Value change");

        Map<String, BigDecimal> sorted = sortByValue(changeStockPriceByCompany);
        int count = 0;
        Iterator<String> itr = sorted.keySet().iterator();
        while (itr.hasNext() && count < 5) {
            System.out.printf("%-80s %10g %s\n", itr.next(), sorted.get(itr.next()), "%");
            count++;
        }
        System.out.println("==================================================================================================");
        System.out.println("==================================================================================================");
    }

    private Map<String, BigDecimal> sortByValue(Map<String, BigDecimal> map) {
        List<Map.Entry<String, BigDecimal>> list = new ArrayList(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Collections.reverse(list);
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Map.Entry<String, BigDecimal> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private List<CompanyData> getHighestVol(List<CompanyData> data) {
        return data.stream().sorted(new CompanyDataVolumeComparator()).limit(5).collect(Collectors.toList());
    }

    private BigDecimal getValueChange(BigDecimal a, BigDecimal b) {
        if (a == null || b == null) return BigDecimal.valueOf(0);
        MathContext mc = new MathContext(10);
        if (a.compareTo(b) < 0) {
            return (b.subtract(a, mc)).divide(a, mc);
        }
        return (a.subtract(b, mc)).divide(a, mc).multiply(BigDecimal.valueOf(100), mc);
    }

    @CacheEvict(value = "companydata", allEntries = true)
    public void clearCache() {
    }
}
