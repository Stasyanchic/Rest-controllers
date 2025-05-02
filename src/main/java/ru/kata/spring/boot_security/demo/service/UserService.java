

package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.dto.UserRequestDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserDtoById(Long id);

    UserResponseDto createUser(UserRequestDto userDto);

    UserResponseDto updateUser(Long id, UserRequestDto userDto);

    void deleteUser(Long id);

    User findByName(String name);

    List<Role> getAllRoles();

}