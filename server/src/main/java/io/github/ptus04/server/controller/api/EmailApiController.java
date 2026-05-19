package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.service.EmailService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/test/email")
@RequiredArgsConstructor
public class EmailApiController {

    private final EmailService emailService;

    public record TestEmailRequest(
            @NotBlank @Email String email,
            @NotBlank String orderCode,
            @NotBlank String invoiceLink
    ) {}

    @PostMapping
    public ResponseEntity<?> testSendEmail(@Valid @RequestBody TestEmailRequest req) {
        emailService.sendOrderEmail(req.email(), req.orderCode(), req.invoiceLink());
        return ResponseEntity.ok(Map.of(
                "message", "Email đang được gửi (async)",
                "to", req.email(),
                "orderCode", req.orderCode()
        ));
    }
}