package org.example.controller.secured;

import org.example.entity.User;
import org.example.entity.UserRole;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/super-admin")
public class PrivateSuperAdminController {

    private final UserService userService;

    @Autowired
    public PrivateSuperAdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/make-user-admin")
    public String makeUserAdmin(@RequestParam int id){

        Optional<User> userToBeUpgradedOptional = userService.findById(id);
        if(userToBeUpgradedOptional.isEmpty()){
            return "redirect:/admin";
        }

        User userToBeUpgraded = userToBeUpgradedOptional.get();
        if(userToBeUpgraded.isSuperAdmin()){
            return "redirect:/admin";
        }

        userService.updateRole(id, UserRole.ADMIN);
        return "redirect:/admin";
    }
}
