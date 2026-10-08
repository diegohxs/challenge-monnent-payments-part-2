package com.monnetpayments.challenge_monnet.domain.entities;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    private String name;

    private String catchPhrase;

    private String bs;
}