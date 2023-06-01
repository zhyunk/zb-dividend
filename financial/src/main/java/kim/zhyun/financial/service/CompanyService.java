package kim.zhyun.financial.service;

import kim.zhyun.financial.model.Company;
import kim.zhyun.financial.model.ScrapedResult;
import kim.zhyun.financial.persist.CompanyRepository;
import kim.zhyun.financial.persist.DividendRepository;
import kim.zhyun.financial.persist.entity.CompanyEntity;
import kim.zhyun.financial.persist.entity.DividendEntity;
import kim.zhyun.financial.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie; // 자동 완성을 위함
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

    // tri에 회사명 저장
    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    // trie에서 회사명 리스트 조회
    public List<String> autocomplete(String keyword) {
        return (List<String>) trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    // tri에 저장된 키워드 삭제
    public void deleteAutocompleteKeyword(String keyword) {
        this.trie.remove(keyword);
    }
}
