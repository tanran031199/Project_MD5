package com.example.lamlaisecurity.repository;

import com.example.lamlaisecurity.entity.Role;
import com.example.lamlaisecurity.config.constant.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Boolean existsByRoleName(RoleName roleName);

    Role findByRoleName(RoleName roleName);
}
