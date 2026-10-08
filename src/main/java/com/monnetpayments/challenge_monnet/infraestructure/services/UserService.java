package com.monnetpayments.challenge_monnet.infraestructure.services;

import com.monnetpayments.challenge_monnet.domain.entities.User;
import com.monnetpayments.challenge_monnet.domain.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserService {

    private final JsonPlaceholderClient jsonPlaceholderClient;
    private final UserRepository userRepository;

    public UserService(
            JsonPlaceholderClient jsonPlaceholderClient,
            UserRepository userRepository) {
        this.jsonPlaceholderClient = jsonPlaceholderClient;
        this.userRepository = userRepository;
    }


    public void loadUsers() {
        List<User> users = jsonPlaceholderClient.getUsers();
        userRepository.saveAll(users);
    }
}
