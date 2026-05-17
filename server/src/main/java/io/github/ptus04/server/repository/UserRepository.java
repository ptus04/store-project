package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.enums.UserRoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhone(String phone);

    long countByRole(UserRoleEnum role);
}