package com.monnetpayments.challenge_monnet.domain.repositories;

import com.monnetpayments.challenge_monnet.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}