package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.RoleDao;
import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.dto.request.UserLoginRequestDto;
import by.slava_borisov.library.dto.request.UserRegistrationRequestDto;
import by.slava_borisov.library.dto.response.UserResponseDto;
import by.slava_borisov.library.mapper.UserMapper;
import by.slava_borisov.library.model.Role;
import by.slava_borisov.library.model.User;
import by.slava_borisov.library.service.AuthService;
import by.slava_borisov.library.util.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_USER_ROLE = "ROLE_USER";

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userDao.existsByUsername(requestDto.username())) {
            throw new IllegalArgumentException(Messages.USERNAME_ALREADY_EXISTS);
        }

        if (userDao.existsByEmail(requestDto.email())) {
            throw new IllegalArgumentException(Messages.EMAIL_ALREADY_EXISTS);
        }

        Role userRole = roleDao.findByName(DEFAULT_USER_ROLE)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.ROLE_NOT_FOUND_BY_NAME.formatted(DEFAULT_USER_ROLE)
                ));

        User user = userMapper.toEntity(requestDto);
        user.setRoles(Set.of(userRole));
        user.setIsActive(true);

        User savedUser = userDao.save(user);
        return userMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto login(UserLoginRequestDto requestDto) {
        User user = userDao.findByUsername(requestDto.username())
                .orElseThrow(() -> new IllegalArgumentException(Messages.INVALID_USERNAME_OR_PASSWORD));

        if (!user.getPassword().equals(requestDto.password())) {
            throw new IllegalArgumentException(Messages.INVALID_USERNAME_OR_PASSWORD);
        }

        return userMapper.toResponseDto(user);
    }
}