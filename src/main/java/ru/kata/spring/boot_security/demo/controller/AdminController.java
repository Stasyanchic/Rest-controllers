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
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
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
    public String getHomePage(@AuthenticationPrincipal User user, Model model) {
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
    @GetMapping("/show")
    public String showUserDetails( Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByName(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with username " + username + " not found");
        }
        UserDto userDto = userService.getUserDtoById(user.getId());
        if (userDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + user.getId() + " not found");
        }
        model.addAttribute("userDto", userDto);
        return "users/show";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String showCreationOfUserForm(Model model) {
        UserDto userDto = new UserDto();
        List<Role> roles = userService.getAllRoles();
        userDto.setRoles(new ArrayList<>());
        model.addAttribute("rolesList", roles);
        model.addAttribute("userDto", userDto);
        return "users/new";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/new")
    public String createUserButton(@ModelAttribute("userDto") UserDto userDto) {
        userService.createUser(userDto);
        return "redirect:/admin/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String updateUserButton(@PathVariable("id") long id, @ModelAttribute("userDto") UserDto userDto) {
        userDto.setId(id);
        userService.updateUser(userDto);
        return "redirect:/admin/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") long id, Model model) {
        UserDto userDto = userService.getUserDtoById(id);
        List<Role> roles = userService.getAllRoles();
        if (userDto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + id + " not found");
        }
        model.addAttribute("userDto", userDto);
        model.addAttribute("rolesList", roles);
        return "users/edit";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteUserButton(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin/list";
    }
}
