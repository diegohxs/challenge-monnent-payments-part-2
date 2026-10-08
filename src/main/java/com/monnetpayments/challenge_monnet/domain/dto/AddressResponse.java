package com.monnetpayments.challenge_monnet.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    private String street;
    private String suite;
    private String city;
    private String zipcode;
    private GeoResponse geo;
}