package kim.zhyun.financial.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
public class CompanyController {

    // 자동완성 api
    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        return null;
    }

    // 회사 조회 api
    @GetMapping
    public ResponseEntity<?> searchCompany() {
        return null;
    }

    // 배당금 데이터 저장 api
    @PostMapping
    public ResponseEntity<?> addCompany() {
        return null;
    }

    // 배당금 데이터 삭제 api
    @DeleteMapping
    public ResponseEntity<?> deleteCompany() {
        return null;
    }
}
