package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.request.EmployeeAccountStatusUpdateRequest;
import io.github.ptus04.server.dto.request.EmployeeCreateRequest;
import io.github.ptus04.server.dto.request.EmployeeUpdateRequest;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.enums.UserRoleEnum;
import io.github.ptus04.server.dto.internal.CustomUserDetails;
import io.github.ptus04.server.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
@RequiredArgsConstructor
public class EmployeeApiController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllEmployees(
            @RequestParam(required = false) Optional<String> role
    ) {
        List<UserResponse> employees;
        if (role.isPresent()) {
            employees = userService.getUsersByRole(UserRoleEnum.valueOf(role.get()));
        } else {
            employees = userService.getAllUsers();
        }
        return ResponseEntity.ok(employees);
    }

    @GetMapping(params = {"page", "size"})
    public ResponseEntity<Page<UserResponse>> getEmployeesPaged(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Optional<String> role
    ) {
        Page<UserResponse> employees;
        if (role.isPresent()) {
            employees = userService.getUsersByRolePaged(UserRoleEnum.valueOf(role.get()), page, size);
        } else {
            employees = userService.getAllUsersPaged(page, size);
        }
        return ResponseEntity.ok(employees);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createEmployee(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody EmployeeCreateRequest request
    ) {
        UserResponse newEmployee = userService.createEmployee(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newEmployee);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateEmployee(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeUpdateRequest request
    ) {
        UserResponse updatedEmployee = userService.updateEmployee(currentUser.getId(), id, request);
        return ResponseEntity.ok(updatedEmployee);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateAccountStatus(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeAccountStatusUpdateRequest request
    ) {
        boolean isDisabling = request.disabledAt() != null;
        UserResponse updatedUser = userService.updateEmployeeAccountStatus(
                currentUser.getId(),
                id,
                isDisabling
        );
        return ResponseEntity.ok(updatedUser);
    }
}
