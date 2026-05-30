package io.github.ptus04.server.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SePayInvoiceCreateRequest {
    @JsonProperty("template_code")
    private String templateCode = "2";
    @JsonProperty("invoice_series")
    private String invoiceSeries = "C26TSE";
    @JsonProperty("issued_date")
    private String issuedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    private String currency = "VND";
    @JsonProperty("payment_method")
    private String paymentMethod = "CK";
    @JsonProperty("provider_account_id")
    private String providerAccountId = "abae54f4-4ed3-11f1-b21a-a6006ab65aca";
    private Buyer buyer = new Buyer();
    private List<Item> items = new ArrayList<>();
    @JsonProperty("is_draft")
    private boolean isDraft = true;

    @Getter
    @Setter
    public static class Buyer {
        private String type = "personal";
        private String name;
        private String address;
        private String email;
        private String phone;
        @JsonProperty("buyer_code")
        private String buyerCode;
    }

    @Getter
    @Setter
    public static class Item {
        @JsonProperty("line_number")
        private int lineNumber;
        @JsonProperty("line_type")
        private int lineType = 1;
        @JsonProperty("item_code")
        private String itemCode;
        @JsonProperty("item_name")
        private String itemName;
        private String unit = "cái";
        private int quantity;
        @JsonProperty("unit_price")
        private BigDecimal unitPrice;
        @JsonProperty("discount_tax")
        private float discountTax;
    }
}
