package com.monnetpayments.challenge_monnet.domain.mappers;

import com.monnetpayments.challenge_monnet.domain.dto.AddressResponse;
import com.monnetpayments.challenge_monnet.domain.dto.CompanyResponse;
import com.monnetpayments.challenge_monnet.domain.dto.GeoResponse;
import com.monnetpayments.challenge_monnet.domain.dto.UserResponse;
import com.monnetpayments.challenge_monnet.domain.entities.Address;
import com.monnetpayments.challenge_monnet.domain.entities.Company;
import com.monnetpayments.challenge_monnet.domain.entities.Geo;
import com.monnetpayments.challenge_monnet.domain.entities.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    AddressResponse toResponse(Address address);

    GeoResponse toResponse(Geo geo);

    CompanyResponse toResponse(Company company);
}