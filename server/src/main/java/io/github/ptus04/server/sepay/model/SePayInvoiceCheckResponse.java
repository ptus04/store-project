package io.github.ptus04.server.sepay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SePayInvoiceCheckResponse {
    private boolean success;
    private Data data;

    @Getter
    @Setter
    public static class Data {
        @JsonProperty("reference_code")
        private String referenceCode;
        private String status;
        private String message;
        private Invoice invoice;

        @Getter
        @Setter
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class Invoice {
            @JsonProperty("pdf_url")
            private String pdfUrl;
        }
    }
}
