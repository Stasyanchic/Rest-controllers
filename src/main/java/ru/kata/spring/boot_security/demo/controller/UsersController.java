package ru.kata.spring.boot_security.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDto;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    private UserDto convertToDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getEmail(),
                user.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList()));

    }

    private User convertToEntity(UserDto userDto) {
        User user = new User();
        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }


    @GetMapping("/list")
    public String showUserList(Model model) {
        List<UserDto> userDtos = userService.getAllUsers().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());


        model.addAttribute("users", userDtos);
        return "users/list";
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable("id") long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found");
        }
        UserDto userDto = convertToDto(user);
        model.addAttribute("userDto", userDto);
        return "users/show";
    }

    @GetMapping("/new")
    public String showNewUserForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "users/new";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("userDto") UserDto userDto) {
        User user = convertToEntity(userDto);
        userService.createUser(user);
        return "redirect:/users/list";
    }

    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable("id") long id, @ModelAttribute("userDto") UserDto userDto) {
        User user = convertToEntity(userDto);
        user.setId(id);
        userService.updateUser(user);
        return "redirect:/users/list";
    }

    @GetMapping("/{id}/edit")
    public String showEditUserForm(@PathVariable("id") long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found");
        }
        UserDto userDto = convertToDto(user);
        model.addAttribute("userDto", userDto);
        return "users/edit";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/users/list";
    }
}
