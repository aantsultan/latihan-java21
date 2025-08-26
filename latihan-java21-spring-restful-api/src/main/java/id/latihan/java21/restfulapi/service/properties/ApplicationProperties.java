package id.latihan.java21.restfulapi.service.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApplicationProperties {

    @Value("${jwt.token.secret}")
    private String jwtTokenSecret;

    @Value("${jwt.token.expire}")
    private String jwtTokenExpire;

}
