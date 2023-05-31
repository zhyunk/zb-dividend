package kim.zhyun.financial;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class ScrapingTest {
    void print() {
        try {
            Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/history?period1=1653868927&period2=1685404927&interval=1d&filter=history&frequency=1d&includeAdjustedClose=true");
            Document document = connection.get(); // get 메서드 요청

            Elements elements = document.getElementsByAttributeValue("data-test", "historical-prices");// 가져올 속성의 정보 (키, 값) -> <div 키="값"></div>
            Element element = elements.get(0); // element = table > [ thead > tr > th ] , [ tbody > tr > th ] , [ tfoot > tr > th ]

            Element tbody = element.children().get(1);
            for (Element e: tbody.children()) {
                String txt = e.text();

                if (!txt.endsWith("Dividend"))
                    continue;

                String[] splits = txt.split(" ");
                String month = splits[0];
                int day  = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                System.out.println("year = " + year);
                System.out.println("month = " + month);
                System.out.println("day = " + day);
                System.out.println("dividend = " + dividend);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
