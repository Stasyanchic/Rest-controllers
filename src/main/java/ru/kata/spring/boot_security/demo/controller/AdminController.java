package ru.kata.spring.boot_security.demo.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.dto.UserRequestDto;
import ru.kata.spring.boot_security.demo.dto.UserResponseDto;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final HttpServletRequest request;

    @Autowired
    public AdminController(UserService userService, HttpServletRequest request) {
        this.userService = userService;
        this.request = request;
    }

    @GetMapping("/sendCSRF")
    public ResponseEntity<String> sendCSRF() {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        return ResponseEntity.ok(csrfToken.getToken());
    }

    @GetMapping("/authorizedUser")
    public ResponseEntity<UserResponseDto> getAuthorizedUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserDtoById(user.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(userService.getAllRoles());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}/get-data")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDtoById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/create-data")
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto createdUser = userService.createUser(userRequestDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/update-data")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(userService.updateUser(id, userRequestDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}/edit-data")
    public ResponseEntity<Map<String, Object>> getUserEditData(@PathVariable Long id) {
        UserResponseDto userDto = userService.getUserDtoById(id);
        List<Role> roles = userService.getAllRoles();

        Map<String, Object> response = new HashMap<>();
        response.put("user", userDto);
        response.put("allRoles", roles);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @GetMapping
    public ModelAndView getAdminPanel(@AuthenticationPrincipal User authenticatedUser, Model model) {
        UserResponseDto currentUser = userService.getUserDtoById(authenticatedUser.getId());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allRoles", userService.getAllRoles());
        return new ModelAndView("dashboard");
    }

    @GetMapping("/table")
    public ModelAndView showUsersPage(@AuthenticationPrincipal User authenticatedUser, Model model) {
        UserResponseDto currentUser = userService.getUserDtoById(authenticatedUser.getId());
        model.addAttribute("currentUser", currentUser);
        return new ModelAndView("users");
    }


    @GetMapping("/home")
    public ModelAndView getUserHomePage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User userToShow = userService.findByName(userName);
        model.addAttribute("user", userToShow);
        return new ModelAndView("profile");
    }

}