package com.monnetpayments.challenge_monnet.infraestructure.controllers;

import com.monnetpayments.challenge_monnet.application.services.ApplicationService;
import com.monnetpayments.challenge_monnet.domain.dto.UserResponse;
import com.monnetpayments.challenge_monnet.domain.entities.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;

@RestController
@RequestMapping
@SecurityRequirement(name = "X-API-TOKEN")
public class UserController {

    private final ApplicationService applicationService;

    public UserController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return applicationService.getUser();
    }
}
