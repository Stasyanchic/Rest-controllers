

package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDto;

import java.util.List;


public interface UserService {

    List<User> getAllUsers();

    User getUserById(long id);

    void createUser(User user);

    void updateUser(User user);

    void deleteUser(Long id);

    User findByName(String name);

    UserDto getUserDtoById(Long id);
}