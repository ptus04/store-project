package io.github.ptus04.server.entity;

import io.github.ptus04.server.enums.UserGenderEnum;
import io.github.ptus04.server.enums.UserRoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users", schema = "storedb", uniqueConstraints = {
        @UniqueConstraint(name = "users_pk_2",
                columnNames = {"phone"}),
        @UniqueConstraint(name = "users_pk_3",
                columnNames = {"email"})})
public class User {
    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Size(max = 128)
    @NotNull
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Size(max = 10)
    @NotNull
    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @Size(max = 64)
    @Column(name = "email", length = 64)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role", nullable = false)
    private UserRoleEnum role;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "gender")
    private UserGenderEnum gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Size(max = 255)
    @Column(name = "avatar", length = 255)
    private String avatar;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false)
    private Instant updatedAt;
}