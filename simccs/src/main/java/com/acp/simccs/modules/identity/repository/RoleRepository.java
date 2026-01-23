package com.acp.simccs.modules.identity.repository;

import com.acp.simccs.modules.identity.model.ERole;
import com.acp.simccs.modules.identity.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}