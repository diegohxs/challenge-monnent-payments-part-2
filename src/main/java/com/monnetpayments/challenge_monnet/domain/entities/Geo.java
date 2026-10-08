package com.monnetpayments.challenge_monnet.domain.entities;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Geo {

    private String lat;

    private String lng;
}
