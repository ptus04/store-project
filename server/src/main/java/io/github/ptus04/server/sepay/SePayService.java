package io.github.ptus04.server.sepay;

import io.github.ptus04.server.entity.Order;
import io.github.ptus04.server.entity.OrderDetail;
import io.github.ptus04.server.entity.Product;
import io.github.ptus04.server.repository.ProductRepository;
import io.github.ptus04.server.sepay.config.SePayProperties;
import io.github.ptus04.server.sepay.model.SePayInvoiceCheckResponse;
import io.github.ptus04.server.sepay.model.SePayInvoiceCreateRequest;
import io.github.ptus04.server.sepay.model.SePayInvoiceCreateResponse;
import io.github.ptus04.server.sepay.model.SePayTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SePayService {
    private static final String SEPAY_TOKEN_URL = "https://einvoice-api-sandbox.sepay.vn/v1/token/";
    private static final String SEPAY_INVOICE_CREATE_URL = "https://einvoice-api-sandbox.sepay.vn/v1/invoices/create/";
    private static final String SEPAY_INVOICE_CHECK_URL = "https://einvoice-api-sandbox.sepay.vn/v1/invoices/create/check/";

    private final RestClient restClient;
    private final SePayProperties sePayProperties;
    private final ProductRepository productRepository;

    private Instant lastGetAccessToken = Instant.now();
    private volatile SePayTokenResponse sePayTokenResponse;

    @Async
    public CompletableFuture<SePayInvoiceCheckResponse> checkInvoice(String trackingCode) {
        ensureValidToken();

        SePayInvoiceCheckResponse response = injectAuthorizationBearerHeader(restClient.get().uri(SEPAY_INVOICE_CHECK_URL + trackingCode))
                .retrieve()
                .body(SePayInvoiceCheckResponse.class);
        return CompletableFuture.completedFuture(response);
    }

    @Async
    public CompletableFuture<SePayInvoiceCreateResponse> createInvoice(Order order) {
        ensureValidToken();

        SePayInvoiceCreateRequest.Buyer buyer = new SePayInvoiceCreateRequest.Buyer();
        buyer.setBuyerCode(order.getUser().getId().toString());
        buyer.setName(order.getUser().getName());
        buyer.setEmail(order.getUser().getEmail());
        buyer.setPhone(order.getUser().getPhone());
        buyer.setAddress(order.getOrderShippingAddress().toAddressString());

        List<UUID> productIds = order.getOrderDetails().stream()
                .map(detail -> detail.getProduct().getId())
                .toList();

        Map<UUID, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        List<SePayInvoiceCreateRequest.Item> items = new ArrayList<>();
        int lineNumber = 1;

        for (OrderDetail detail : order.getOrderDetails()) {
            UUID productId = detail.getProduct().getId();
            Product product = productMap.get(productId);

            if (product == null) {
                throw new IllegalStateException("Không tìm thấy sản phẩm: " + productId);
            }

            SePayInvoiceCreateRequest.Item item = new SePayInvoiceCreateRequest.Item();
            item.setLineNumber(lineNumber++);
            item.setItemCode(product.getId().toString());

            String itemName = product.getName();
            if (detail.getProductSize() != null) {
                itemName += " (Size: " + detail.getProductSize() + ")";
            }
            item.setItemName(itemName);

            item.setQuantity(detail.getQuantity());
            item.setUnitPrice(detail.getPrice());
            items.add(item);
        }

        SePayInvoiceCreateRequest request = new SePayInvoiceCreateRequest();
        request.setBuyer(buyer);
        request.setItems(items);
        request.setDraft(false);

        SePayInvoiceCreateResponse response = injectAuthorizationBearerHeader(restClient.post().uri(SEPAY_INVOICE_CREATE_URL))
                .body(request)
                .retrieve()
                .requiredBody(SePayInvoiceCreateResponse.class);

        return CompletableFuture.completedFuture(response);
    }

    private void ensureValidToken() {
        if (sePayTokenResponse == null || Instant.now().isAfter(lastGetAccessToken.plusSeconds(sePayTokenResponse.getData().getExpiresIn()))) {
            synchronized (this) {
                if (sePayTokenResponse == null || Instant.now().isAfter(lastGetAccessToken.plusSeconds(sePayTokenResponse.getData().getExpiresIn()))) {
                    SePayTokenResponse tokenResponse = injectAuthorizationBasicHeader(restClient.post().uri(SEPAY_TOKEN_URL))
                            .retrieve()
                            .requiredBody(SePayTokenResponse.class);
                    lastGetAccessToken = Instant.now();
                    this.sePayTokenResponse = tokenResponse;
                }
            }
        }
    }

    public RestClient.RequestBodySpec injectAuthorizationBasicHeader(RestClient.RequestBodySpec spec) {
        byte[] rawToken = (sePayProperties.getUsername() + ":" + sePayProperties.getPassword()).getBytes();
        String authToken = Base64.getEncoder().encodeToString(rawToken);
        return spec.header("Authorization", "Basic " + authToken);
    }

    @SuppressWarnings("unchecked")
    public <T extends RestClient.RequestHeadersSpec<?>> T injectAuthorizationBearerHeader(T spec) {
        return (T) spec.header("Authorization", "Bearer " + sePayTokenResponse.getData().getAccessToken());
    }
}
