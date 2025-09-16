package id.latihan.java21.restfulapi.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VectorServiceTest {

    @Autowired
    private VectorService vectorService;

    @Test
    void loadData(){
        vectorService.loadData();
    }

}
