package org.example.controller.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.User;
import org.example.entity.UserRole;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Set;

@Controller
public class PublicAuthorizationController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository;

    @Autowired
    public PublicAuthorizationController(UserService userService,
                                         PasswordEncoder passwordEncoder,
                                         AuthenticationManager authenticationManager,
                                         SecurityContextRepository securityContextRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager =authenticationManager;
        this.securityContextRepository = securityContextRepository;

    }


    @GetMapping("/login")
    public String getLoginPage(Model model, @RequestParam(required = false) String error){
        if (error != null){
            model.addAttribute("isAuthenticationFailed", true);
        }
        return "public/authorization/login-page";
    }

    @GetMapping("/registration")
    public String getRegistrationPage(){
        return "public/authorization/registration-page";
    }

    @PostMapping("/registration")
    public String createUserAccount(@RequestParam String name,
                                    @RequestParam String email,
                                    @RequestParam String password,
                                    HttpServletRequest request,
                                    HttpServletResponse response){
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(name, email, encodedPassword, UserRole.USER);
        userService.save(user);
        forceAutoLogin(email, password, request, response);
        return "redirect:/account";
    }

    private void forceAutoLogin(String email,
                                String rawPassword,
                                HttpServletRequest request,
                                HttpServletResponse response) {


        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(email, rawPassword)
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);
    }
}
