package id.latihan.java21.restfulapi.service;

import id.latihan.java21.restfulapi.dto.UserDto;
import id.latihan.java21.restfulapi.exception.ApplicationException;
import id.latihan.java21.restfulapi.model.User;
import id.latihan.java21.restfulapi.repository.UserRepository;
import id.latihan.java21.restfulapi.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String INCORRECT_USERNAME_OR_PASSWORD = "Username or Password is incorrect!";

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public String register(UserDto userDto) {
        String password = userDto.getPassword();
        String passwordConfirm = userDto.getPasswordConfirm();

        if (ObjectUtils.isEmpty(password)
                || ObjectUtils.isEmpty(passwordConfirm)
                || !password.equalsIgnoreCase(passwordConfirm)) {
            throw new ApplicationException("Password is not match !");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setStatus(true);
        user.setCreatedBy(0L);

        repository.save(user);

        return "OK";
    }

    @Override
    public String login(UserDto userDto) {
        String username = userDto.getUsername();
        String password = userDto.getPassword();
        String key = String.format("token:%s", username);
        String token = redisTemplate.opsForValue().get(key);
        if (ObjectUtils.isEmpty(token)) {
            token = createNewToken(username, password, key);
        } else {
            Date expiration = jwtService.extractExpiration(token);
            if (new Date().after(expiration)) { // check expire time
                token = createNewToken(username, password, key);
            }
        }
        return token;
    }

    private String createNewToken(String username, String password, String key) {
        Optional<User> byUsername = repository.findByUsername(username);
        if (byUsername.isEmpty()) throw new ApplicationException(INCORRECT_USERNAME_OR_PASSWORD);
        User user = byUsername.get();
        String currentPassword = user.getPassword();
        boolean matches = passwordEncoder.matches(password, currentPassword);
        if (!matches) throw new ApplicationException(INCORRECT_USERNAME_OR_PASSWORD);
        String token = jwtService.generateToken(username);
        redisTemplate.opsForValue().set(key, token, Duration.ofMinutes(30));
        return token;
    }
}
