package io.github.ptus04.server;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    @Test
    public void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Chituongdev24@";
        String hash = encoder.encode(password);
        System.out.println("HASH_START[" + hash + "]HASH_END");
    }
}
