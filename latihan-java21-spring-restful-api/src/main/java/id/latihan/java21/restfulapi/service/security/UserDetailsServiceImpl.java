package id.latihan.java21.restfulapi.service.security;

import id.latihan.java21.restfulapi.exception.ApplicationException;
import id.latihan.java21.restfulapi.model.User;
import id.latihan.java21.restfulapi.model.security.CurrentUser;
import id.latihan.java21.restfulapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = repository.findByUsername(username);

        if(userOptional.isEmpty()){
            throw new ApplicationException("User does not exist !");
        }

        User user = userOptional.get();
        return new CurrentUser(user);
    }
}
