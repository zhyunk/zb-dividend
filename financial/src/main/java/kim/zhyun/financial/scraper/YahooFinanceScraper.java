package kim.zhyun.financial.scraper;

import kim.zhyun.financial.model.Company;
import kim.zhyun.financial.model.Dividend;
import kim.zhyun.financial.model.ScrapedResult;
import kim.zhyun.financial.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class YahooFinanceScraper implements Scraper {

    private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1d";
    private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s"; // 회사명 가져오는 url
    private static final long START_TIME = 86400; // 60초 * 60분 * 24시간

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult();
        scrapResult.setCompany(company);

        try {
            long now = System.currentTimeMillis() / 1000; // 밀리초 / 1000 = 초

            String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get(); // get 메서드 요청

            Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");// 가져올 속성의 정보 (키, 값) -> <div 키="값"></div>
            Element table = parsingDivs.get(0); // element = table > [ thead > tr > th ] , [ tbody > tr > th ] , [ tfoot > tr > th ]

            Element tbody = table.children().get(1);

            List<Dividend> dividends = new ArrayList<>();
            for (Element e: tbody.children()) {
                String txt = e.text();

                if (!txt.endsWith("Dividend"))
                    continue;

                String[] splits = txt.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day  = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if (month < 0)
                    throw new RuntimeException("Unexpected Month enum value -> " + splits[0]);

                dividends.add(Dividend.builder()
                                        .date(LocalDateTime.of(year, month, day, 0, 0))
                                        .dividend(dividend)
                                        .build());
            }
            scrapResult.setDividendEntities(dividends);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").get(0);
            String title = titleEle.text().split(" - ")[1].trim();

            return Company.builder()
                            .ticker(ticker)
                            .name(title)
                            .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
