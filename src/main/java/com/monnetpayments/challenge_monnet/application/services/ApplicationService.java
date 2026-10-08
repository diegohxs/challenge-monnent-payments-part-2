package com.monnetpayments.challenge_monnet.application.services;

import com.monnetpayments.challenge_monnet.domain.dto.UserResponse;
import com.monnetpayments.challenge_monnet.domain.mappers.UserMapper;
import com.monnetpayments.challenge_monnet.domain.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public ApplicationService(
            UserRepository userRepository,
            UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> getUser() {
        return userMapper.toResponseList(userRepository.findAll());
    }
}