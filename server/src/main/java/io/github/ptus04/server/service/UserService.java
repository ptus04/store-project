package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.request.UserProfileUpdateRequest;
import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.enums.UserGenderEnum;
import io.github.ptus04.server.enums.UserRoleEnum;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse getUserById(UUID id);

    UserResponse updateProfile(UUID id, UserProfileUpdateRequest request);

    long countByRole(UserRoleEnum role);

    List<UserResponse> getUsersByRole(UserRoleEnum role);

    Page<UserResponse> getUsersByRolePaged(UserRoleEnum role, int page, int size);

    List<UserResponse> getAllUsers();

    Page<UserResponse> getAllUsersPaged(int page, int size);

    UserResponse updateEmployeeAccountStatus(UUID actorId, UUID targetId, boolean disabled);

    Page<UserResponse> searchCustomers(UserGenderEnum gender, String search, int page, int size);

}
