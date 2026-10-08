package com.monnetpayments.challenge_monnet.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Long id;

    private String name;

    private String username;

    private String email;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "street",
                    column = @Column(name = "address_street")
            ),
            @AttributeOverride(
                    name = "suite",
                    column = @Column(name = "address_suite")
            ),
            @AttributeOverride(
                    name = "city",
                    column = @Column(name = "address_city")
            ),
            @AttributeOverride(
                    name = "zipcode",
                    column = @Column(name = "address_zipcode")
            ),
            @AttributeOverride(
                    name = "geo.lat",
                    column = @Column(name = "address_lat")
            ),
            @AttributeOverride(
                    name = "geo.lng",
                    column = @Column(name = "address_lng")
            )
    })
    private Address address;

    private String phone;

    private String website;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "name",
                    column = @Column(name = "company_name")
            ),
            @AttributeOverride(
                    name = "catchPhrase",
                    column = @Column(name = "company_catch_phrase")
            ),
            @AttributeOverride(
                    name = "bs",
                    column = @Column(name = "company_bs")
            )
    })
    private Company company;
}