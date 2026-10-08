package com.monnetpayments.challenge_monnet.domain.entities;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String street;

    private String suite;

    private String city;

    private String zipcode;

    @Embedded
    private Geo geo;
}