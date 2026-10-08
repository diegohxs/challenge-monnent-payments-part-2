package com.monnetpayments.challenge_monnet.application.services;

import com.monnetpayments.challenge_monnet.domain.dto.UserResponse;
import com.monnetpayments.challenge_monnet.domain.entities.User;
import com.monnetpayments.challenge_monnet.domain.mappers.UserMapper;
import com.monnetpayments.challenge_monnet.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void shouldReturnUsers() {
        List<User> users = List.of(
                new User(),
                new User()
        );

        List<UserResponse> userResponses = List.of(
                new UserResponse(),
                new UserResponse()
        );

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toResponseList(users)).thenReturn(userResponses);

        List<UserResponse> result = applicationService.getUser();

        assertEquals(userResponses, result);

        verify(userRepository).findAll();
        verify(userMapper).toResponseList(users);
    }
}