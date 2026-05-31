package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.enums.UserGenderEnum;
import io.github.ptus04.server.dto.internal.CustomUserDetails;
import io.github.ptus04.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
@RequiredArgsConstructor
public class CustomerApiController {

    private final UserService userService;

    /**
     * GET /api/customers?page=0&size=10&gender=MALE&search=an
     * Lấy danh sách khách hàng có phân trang, lọc giới tính, tìm kiếm
     */
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) UserGenderEnum gender,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(userService.searchCustomers(gender, search, page, size));
    }

    /**
     * GET /api/customers/{id}
     * Xem chi tiết một khách hàng
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getCustomerById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * PATCH /api/customers/{id}/status?disabled=true
     * Vô hiệu hóa / kích hoạt tài khoản khách hàng
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateCustomerStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id,
            @RequestParam boolean disabled
    ) {
        UserResponse updated = userService.updateEmployeeAccountStatus(
                userDetails.getId(), id, disabled
        );
        return ResponseEntity.ok(updated);
    }
}