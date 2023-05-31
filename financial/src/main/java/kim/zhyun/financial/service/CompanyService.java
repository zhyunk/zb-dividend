package kim.zhyun.financial.service;

import kim.zhyun.financial.model.Company;
import kim.zhyun.financial.model.ScrapedResult;
import kim.zhyun.financial.persist.CompanyRepository;
import kim.zhyun.financial.persist.DividendRepository;
import kim.zhyun.financial.persist.entity.CompanyEntity;
import kim.zhyun.financial.persist.entity.DividendEntity;
import kim.zhyun.financial.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists)
            throw new RuntimeException("already exists ticker -> " + ticker);

        return this.storeCompanyAndDividend(ticker);
    }

    private Company storeCompanyAndDividend(String ticker) {
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company))
            throw new RuntimeException("failed to scrap ticker -> " + ticker);

        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividendEntities().stream()
                                                            .map(dividend -> new DividendEntity(companyEntity.getId(), dividend))
                                                            .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntities);
        return company;
    }
}
