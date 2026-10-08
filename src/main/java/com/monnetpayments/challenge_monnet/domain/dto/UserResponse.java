package com.monnetpayments.challenge_monnet.domain.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String username;
    private String email;
    private AddressResponse address;
    private String phone;
    private String website;
    private CompanyResponse company;
}