package kim.zhyun.financial.scraper;

import kim.zhyun.financial.model.Company;
import kim.zhyun.financial.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
