package io.github.ptus04.server.invoice.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SePayInvoiceCreateResponse {
    private boolean success;
    private Data data;

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Data {
        @JsonProperty("tracking_code")
        private String trackingCode;
        @JsonProperty("tracking_url")
        private String trackingUrl;
        private String message;
    }
}
