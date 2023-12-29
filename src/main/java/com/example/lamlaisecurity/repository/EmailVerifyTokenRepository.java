package com.example.lamlaisecurity.repository;

import com.example.lamlaisecurity.entity.EmailVerifyToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerifyTokenRepository extends JpaRepository<EmailVerifyToken, Long> {
    Optional<EmailVerifyToken> findByToken(String token);
}
