package id.latihan.java21.restfulapi.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService service;

    @Test
    void findAll() {
        service.findAll();
    }

}
