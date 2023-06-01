package kim.zhyun.financial.service;

import kim.zhyun.financial.model.Company;
import kim.zhyun.financial.model.Dividend;
import kim.zhyun.financial.model.ScrapedResult;
import kim.zhyun.financial.persist.CompanyRepository;
import kim.zhyun.financial.persist.DividendRepository;
import kim.zhyun.financial.persist.entity.CompanyEntity;
import kim.zhyun.financial.persist.entity.DividendEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {
        CompanyEntity company = companyRepository
                .findByName(companyName)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다"));

        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());

        List<Dividend> dividends = dividendEntities
                                        .stream()
                                        .map(entity -> Dividend.builder()
                                                                .date(entity.getDate())
                                                                .dividend(entity.getDividend())
                                                                .build())
                                        .collect(Collectors.toList());

        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build(),
                dividends);
    }
}
