package io.github.ptus04.server.repository;

import io.github.ptus04.server.entity.User;
import io.github.ptus04.server.enums.UserGenderEnum;
import io.github.ptus04.server.enums.UserRoleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPhone(String phone);

    long countByRole(UserRoleEnum role);

    List<User> findByRole(UserRoleEnum role);

    Page<User> findByRole(UserRoleEnum role, Pageable pageable);

    @Query("""
            SELECT u FROM User u
            WHERE u.role = :role
              AND (:gender IS NULL OR u.gender = :gender)
              AND (:search IS NULL OR
                   u.name   LIKE %:search% OR
                   u.phone  LIKE %:search% OR
                   u.email  LIKE %:search%)
            ORDER BY u.createdAt DESC
            """)
    Page<User> searchByRoleAndFilters(
            @Param("role") UserRoleEnum role,
            @Param("gender") UserGenderEnum gender,
            @Param("search") String search,
            Pageable pageable);
}