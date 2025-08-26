package id.latihan.java21.spring.ai.controller;

import id.latihan.java21.spring.ai.service.CustomerPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerPurchaseController {

    private final CustomerPurchaseService service;

    @GetMapping(value = "/customer/purchase")
    public String loadData() {
        service.loadData();
        return "OK";
    }

    @GetMapping(value = "/customer/purchase/v2")
    public String loadDataV2() {
        service.loadDataV2();
        return "OK";
    }

}
