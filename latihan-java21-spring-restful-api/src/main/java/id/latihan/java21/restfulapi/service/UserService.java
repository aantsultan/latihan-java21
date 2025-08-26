package id.latihan.java21.restfulapi.service;

import id.latihan.java21.restfulapi.dto.UserDto;
import id.latihan.java21.restfulapi.model.User;

import java.util.List;

public interface UserService {

    List<User> findAll();

    String register(UserDto userDto);

    String login(UserDto userDto);
}
