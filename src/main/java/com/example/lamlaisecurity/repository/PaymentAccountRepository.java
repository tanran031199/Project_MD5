package com.example.lamlaisecurity.repository;

import com.example.lamlaisecurity.entity.PaymentAccount;
import com.example.lamlaisecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, Long> {
    List<PaymentAccount> findAllByUser(User user);

    Boolean existsByCardNumberAndUser(String cardNumber, User user);

    Optional<PaymentAccount> findByPaymentAccountIdAndUser(Long paymentAccountId, User user);
}
