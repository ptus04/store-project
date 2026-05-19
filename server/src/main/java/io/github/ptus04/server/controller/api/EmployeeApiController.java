package io.github.ptus04.server.controller.api;

import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.enums.UserRoleEnum;
import io.github.ptus04.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
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
}



