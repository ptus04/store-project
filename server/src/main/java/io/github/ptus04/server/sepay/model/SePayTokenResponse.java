package io.github.ptus04.server.sepay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SePayTokenResponse {
    private boolean success;
    private Data data;

    @Getter
    @Setter
    public static class Data {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
        @JsonProperty("expires_in")
        private long expiresIn;
    }
}
