package id.latihan.java21.restfulapi.service.jwt;

import id.latihan.java21.restfulapi.exception.ApplicationException;
import id.latihan.java21.restfulapi.service.properties.ApplicationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtService {

    public final ApplicationProperties properties;

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username) {
        int duration = 1;
        String expire = properties.getJwtTokenExpire();
        if (!ObjectUtils.isEmpty(expire) && expire.length() > 2) {
            if (expire.toUpperCase().endsWith("M")) {
                // minute
                expire = expire.replace("m", "").replace("M", "");
                duration = Integer.parseInt(expire);
            } else if (expire.toUpperCase().endsWith("H")) {
                expire = expire.replace("h", "").replace("H", "");
                duration = Integer.parseInt(expire) * 60;
            }
        }
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (1000L * 60 * duration))) // 30 Menit
                .signWith(getSignKey())
                .compact();
    }

    private Key getSignKey() {
        byte[] bytes = Decoders.BASE64.decode(properties.getJwtTokenSecret());
        if (bytes.length < 32) {
            throw new ApplicationException("Secret key is too short !");
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

}
