package ru.kata.spring.boot_security.demo.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showUserList(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "list";
    }

    @GetMapping("/{id}")
    public String showUser(@PathVariable("id") long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            return "redirect:/users";
        }
        model.addAttribute("user", user);
        return "users/show";
    }

    @GetMapping("/new")
    public String showNewUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/new";
    }

    @PostMapping("/new")
    public String createUser(@ModelAttribute("user") User user) {
        userService.createUser(user);
        return "redirect:/users";
    }

    @PostMapping("/{id}/update")
    public String updateUser(@PathVariable("id") long id, @ModelAttribute("user") User user) {
        user.setId(id);
        userService.updateUser(user);
        return "redirect:/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditUserForm(@PathVariable("id") long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {

            return "redirect:/users";
        }
        model.addAttribute("user", user);
        return "users/edit";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }
}
