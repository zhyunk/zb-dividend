package kim.zhyun.financial.service;

import kim.zhyun.financial.model.Company;
import kim.zhyun.financial.model.ScrapedResult;
import kim.zhyun.financial.persist.CompanyRepository;
import kim.zhyun.financial.persist.DividendRepository;
import kim.zhyun.financial.persist.entity.CompanyEntity;
import kim.zhyun.financial.persist.entity.DividendEntity;
import kim.zhyun.financial.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {

        return this.companyRepository.findAll(pageable);
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

    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.stream()
                                .map(entity -> entity.getName())
                                .collect(Collectors.toList());
    }
}
