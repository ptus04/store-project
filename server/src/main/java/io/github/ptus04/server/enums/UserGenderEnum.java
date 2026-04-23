package io.github.ptus04.server.enums;

import lombok.Getter;

@Getter
public enum UserGenderEnum {
    MALE(true),
    FEMALE(false);

    private final boolean gender;

    UserGenderEnum(boolean gender) {
        this.gender = gender;
    }
}
