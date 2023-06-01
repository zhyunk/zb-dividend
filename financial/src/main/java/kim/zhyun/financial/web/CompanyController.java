package kim.zhyun.financial.web;

import kim.zhyun.financial.model.Company;
import kim.zhyun.financial.persist.entity.CompanyEntity;
import kim.zhyun.financial.service.CompanyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@AllArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // 자동완성 api
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        var result = this.companyService.autocomplete(keyword);
        return ResponseEntity.ok(result);
    }

    // 회사 조회 api
    @GetMapping
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }

    // 배당금 데이터 저장 api
    @PostMapping
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker))
            throw new RuntimeException("ticker is empty");

        Company company = this.companyService.save(ticker);

        // 자동완성 값 추가
        this.companyService.addAutocompleteKeyword(company.getName());
        return ResponseEntity.ok(company);
    }

    // 배당금 데이터 삭제 api
    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
