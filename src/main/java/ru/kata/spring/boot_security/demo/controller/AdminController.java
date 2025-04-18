package ru.kata.spring.boot_security.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public String getUserHomePage(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userService.findByName(user.getUsername()));
        return "admin/dashboard";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public String showUserList(Model model) {
        List<UserDto> userDtos = userService.getAllUsers();
        model.addAttribute("users", userDtos);
        return "users/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public String showUser(@PathVariable("id") long id, Model model) {
        UserDto userDto = userService.getUserDtoById(id);
        if (userDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found");
        }
        model.addAttribute("userDto", userDto);
        return "users/show";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String showNewUserForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "users/new";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/new")
    public String createUser(@ModelAttribute("userDto") UserDto userDto) {
        userService.createUser(userDto);
        return "redirect:/users/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable("id") long id, @ModelAttribute("userDto") UserDto userDto) {
        userDto.setId(id);
        userService.updateUser(userDto);
        return "redirect:/users/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/edit")
    public String showEditUserForm(@PathVariable("id") long id, Model model) {
        UserDto userDto = userService.getUserDtoById(id);
        if (userDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found");
        }
        model.addAttribute("userDto", userDto);
        return "users/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/users/list";
    }
}
