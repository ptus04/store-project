package io.github.ptus04.server.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    CUSTOMER((byte) 0),
    EMPLOYEE((byte) 1),
    ADMIN((byte) 2);

    private final byte role;

    UserRoleEnum(byte role) {
        this.role = role;
    }
}
