package com.monnetpayments.challenge_monnet.infraestructure.services;

import com.monnetpayments.challenge_monnet.domain.entities.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "jsonPlaceholderClient",
        url = "https://jsonplaceholder.typicode.com"
)
public interface JsonPlaceholderClient {

    @GetMapping("/users")
    List<User> getUsers();
}
