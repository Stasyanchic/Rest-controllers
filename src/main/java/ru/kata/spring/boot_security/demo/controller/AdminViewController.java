package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private final UserService userService;

    @Autowired
    public AdminViewController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getAdminPanel(@AuthenticationPrincipal User authenticatedUser, Model model) {
        UserResponseDto currentUser = userService.getUserDtoById(authenticatedUser.getId());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allRoles", userService.getAllRoles());

        return "dashboard";
    }

    // Страница с таблицей пользователей (users.html)
    @GetMapping("/users")
    public String showUsersPage(@AuthenticationPrincipal User authenticatedUser, Model model) {
        UserResponseDto currentUser = userService.getUserDtoById(authenticatedUser.getId());
        model.addAttribute("currentUser", currentUser);
        return "users";
    }
}