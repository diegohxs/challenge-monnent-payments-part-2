package com.monnetpayments.challenge_monnet;

import com.monnetpayments.challenge_monnet.infraestructure.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableFeignClients
@SpringBootApplication
public class ChallengeMonnetApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChallengeMonnetApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadUsers(UserService userService) {
        return args -> userService.loadUsers();
    }
}