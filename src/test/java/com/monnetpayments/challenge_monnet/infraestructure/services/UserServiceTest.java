package com.monnetpayments.challenge_monnet.infraestructure.services;

import com.monnetpayments.challenge_monnet.domain.entities.User;
import com.monnetpayments.challenge_monnet.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JsonPlaceholderClient jsonPlaceholderClient;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldLoadUsers() {
        List<User> users = List.of(
                new User(),
                new User()
        );

        when(jsonPlaceholderClient.getUsers()).thenReturn(users);

        userService.loadUsers();

        verify(jsonPlaceholderClient).getUsers();
        verify(userRepository).saveAll(users);
    }
}