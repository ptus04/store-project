package io.github.ptus04.server.service.impl;

import io.github.ptus04.server.dto.response.UserResponse;
import io.github.ptus04.server.exception.UserNotFoundException;
import io.github.ptus04.server.mapper.UserMapper;
import io.github.ptus04.server.repository.UserRepository;
import io.github.ptus04.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse getUserById(UUID id) {
        return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(UserNotFoundException::new));
    }

}
