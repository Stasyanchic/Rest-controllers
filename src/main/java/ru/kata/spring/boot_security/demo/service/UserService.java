

package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.dto.UserDto;

import java.util.List;


public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserDtoById(Long id);

    void createUser(UserDto userDto);

    void updateUser(UserDto userDto);

    void deleteUser(Long id);

    User findByName(String name);

    List<Role> getAllRoles();

}