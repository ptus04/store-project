package io.github.ptus04.invoiceservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "sepay")
public class SePayInvoiceProperties {
    private String username = "";
    private String password = "";
    private String authUrl = "https://einvoice-api-sandbox.sepay.vn/v1/token/";
    private String createUrl = "https://einvoice-api-sandbox.sepay.vn/v1/invoices/create/";
    private String checkUrl = "https://einvoice-api-sandbox.sepay.vn/v1/invoices/create/check/";
}
