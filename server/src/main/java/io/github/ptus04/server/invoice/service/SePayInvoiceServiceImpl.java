package io.github.ptus04.server.invoice.service;

import io.github.ptus04.server.event.InvoiceCreatedEvent;
import io.github.ptus04.server.event.OrderPaidEvent;
import io.github.ptus04.server.invoice.config.SePayInvoiceProperties;
import io.github.ptus04.server.invoice.dto.request.SePayInvoiceCreateRequest;
import io.github.ptus04.server.invoice.dto.response.SePayInvoiceCheckResponse;
import io.github.ptus04.server.invoice.dto.response.SePayInvoiceCreateResponse;
import io.github.ptus04.server.invoice.dto.response.SePayTokenResponse;
import io.github.ptus04.server.invoice.producer.InvoiceEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SePayInvoiceServiceImpl implements InvoiceService {
    private final RestClient restClient;
    private final SePayInvoiceProperties sePayInvoiceProperties;
    private final InvoiceEventProducer invoiceEventProducer;

    private volatile Instant lastGetAccessToken = Instant.now();
    private volatile SePayTokenResponse sePayTokenResponse;

    private static SePayInvoiceCreateRequest.Buyer extractBuyer(OrderPaidEvent orderPaidEvent) {
        SePayInvoiceCreateRequest.Buyer buyer = new SePayInvoiceCreateRequest.Buyer();
        buyer.setBuyerCode(orderPaidEvent.orderId());
        buyer.setName(orderPaidEvent.buyerName());
        buyer.setEmail(orderPaidEvent.email());
        buyer.setPhone(orderPaidEvent.buyerPhone());
        buyer.setAddress(orderPaidEvent.buyerAddress());

        return buyer;
    }

    private static List<SePayInvoiceCreateRequest.Item> extractItems(OrderPaidEvent orderPaidEvent) {
        List<SePayInvoiceCreateRequest.Item> items = new ArrayList<>();
        for (int i = 0; i < orderPaidEvent.items().size(); i++) {
            OrderPaidEvent.OrderItem orderItem = orderPaidEvent.items().get(i);

            SePayInvoiceCreateRequest.Item item = new SePayInvoiceCreateRequest.Item();
            item.setLineNumber(i + 1);
            item.setItemCode(orderItem.itemCode());
            item.setItemName(orderItem.itemName());
            item.setQuantity(orderItem.quantity());
            item.setUnitPrice(orderItem.unitPrice());
            items.add(item);
        }

        return items;
    }

    private static SePayInvoiceCreateRequest createInvoiceCreateRequest(OrderPaidEvent orderPaidEvent) {
        SePayInvoiceCreateRequest request = new SePayInvoiceCreateRequest();
        request.setBuyer(extractBuyer(orderPaidEvent));
        request.setItems(extractItems(orderPaidEvent));
        request.setDraft(false);

        return request;
    }

    @Override
    public void createInvoice(OrderPaidEvent orderPaidEvent) {
        ensureValidToken();

        SePayInvoiceCreateResponse response = injectToken(restClient.post().uri(sePayInvoiceProperties.getCreateUrl()))
                .body(createInvoiceCreateRequest(orderPaidEvent))
                .retrieve()
                .requiredBody(SePayInvoiceCreateResponse.class);

        String invoiceLink = getInvoicePdfUrl(response.getData().getTrackingCode());

        invoiceEventProducer.publishInvoiceCreatedEvent(
                new InvoiceCreatedEvent(
                        orderPaidEvent.email(),
                        orderPaidEvent.orderId(),
                        orderPaidEvent.orderCode(),
                        invoiceLink
                )
        );
    }

    private String getInvoicePdfUrl(String trackingCode) {
        SePayInvoiceCheckResponse response = null;

        int maxAttempts = 3;
        int attempt = 0;
        long backoffMillis = 3000;

        while (attempt < maxAttempts) {
            log.info("Checking invoice status, attempt {} of {}", attempt, maxAttempts);

            response = injectToken(restClient.get().uri(sePayInvoiceProperties.getCheckUrl() + trackingCode))
                    .retrieve()
                    .body(SePayInvoiceCheckResponse.class);
            if (response != null && response.getData().getInvoice() != null) {
                break;
            }

            try {
                Thread.sleep(backoffMillis);
            } catch (InterruptedException e) {
                log.atWarn().setMessage("Invoice check interrupted during backoff").setCause(e).log();
                throw new RuntimeException(e);
            }
            attempt++;
        }

        if (response == null || response.getData().getInvoice() == null) {
            log.atError().setMessage("Failed to retrieve invoice after {} attempts").addArgument(maxAttempts).log();
            throw new RuntimeException("Failed to retrieve invoice after " + maxAttempts + " attempts");
        }

        return response.getData().getInvoice().getPdfUrl();
    }

    private void ensureValidToken() {
        if (isTokenExpired()) {
            synchronized (this) {
                if (isTokenExpired()) {
                    this.sePayTokenResponse = getSePayToken();
                    lastGetAccessToken = Instant.now();
                }
            }
        }
    }

    private boolean isTokenExpired() {
        return sePayTokenResponse == null
                || Instant.now().isAfter(lastGetAccessToken.plusSeconds(sePayTokenResponse.getData().getExpiresIn()));
    }

    private SePayTokenResponse getSePayToken() {
        byte[] rawToken = (sePayInvoiceProperties.getUsername() + ":" + sePayInvoiceProperties.getPassword()).getBytes();
        String authToken = Base64.getEncoder().encodeToString(rawToken);

        return restClient.post()
                .uri(sePayInvoiceProperties.getAuthUrl())
                .header("Authorization", "Basic " + authToken)
                .retrieve()
                .requiredBody(SePayTokenResponse.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends RestClient.RequestHeadersSpec<?>> T injectToken(T spec) {
        return (T) spec.header("Authorization", "Bearer " + sePayTokenResponse.getData().getAccessToken());
    }
}
