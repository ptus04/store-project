package io.github.ptus04.server.util;

public final class PhoneNumberUtils {
    private PhoneNumberUtils() {
    }

    public static String prefixWithVietnameseCode(String phoneNumber) {
        return phoneNumber.startsWith("0") ? "+84" + phoneNumber.substring(1) : phoneNumber;
    }
}
