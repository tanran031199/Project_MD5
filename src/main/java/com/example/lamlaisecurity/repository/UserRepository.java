package com.example.lamlaisecurity.repository;

import com.example.lamlaisecurity.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("select u from User u " +
            "join u.roles r " +
            "where u.fullName = :fullName and r.roleName = 'ROLE_USER'")
    Page<User> findAllByFullNameAndRoleName(Pageable pageable, @Param("fullName") String fullName);

    @Query("select u from User u " +
            "join u.roles r " +
            "where r.roleName = 'ROLE_USER'")
    Page<User> findAllByRoleName(Pageable pageable);
}
