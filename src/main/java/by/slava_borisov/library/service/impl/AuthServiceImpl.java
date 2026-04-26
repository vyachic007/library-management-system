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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
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
        log.info("Регистрация пользователя: username={}, email={}",
                requestDto.username(), requestDto.email());

        if (userDao.existsByUsername(requestDto.username())) {
            log.warn("Попытка регистрации с уже занятым username={}", requestDto.username());
            throw new IllegalArgumentException(Messages.USERNAME_ALREADY_EXISTS);
        }

        if (userDao.existsByEmail(requestDto.email())) {
            log.warn("Попытка регистрации с уже занятым email={}", requestDto.email());
            throw new IllegalArgumentException(Messages.EMAIL_ALREADY_EXISTS);
        }

        Role userRole = roleDao.findByName(DEFAULT_USER_ROLE)
                .orElseThrow(() -> {
                    log.error("Роль по умолчанию не найдена: roleName={}", DEFAULT_USER_ROLE);
                    return new EntityNotFoundException(
                            Messages.ROLE_NOT_FOUND_BY_NAME.formatted(DEFAULT_USER_ROLE)
                    );
                });

        User user = userMapper.toEntity(requestDto);
        user.setRoles(Set.of(userRole));
        user.setIsActive(true);

        User savedUser = userDao.save(user);

        log.info("Пользователь успешно зарегистрирован: id={}, username={}, email={}",
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());

        return userMapper.toResponseDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto login(UserLoginRequestDto requestDto) {
        log.info("Попытка авторизации пользователя: username={}", requestDto.username());

        User user = userDao.findByUsername(requestDto.username())
                .orElseThrow(() -> {
                    log.warn("Ошибка авторизации: пользователь не найден, username={}", requestDto.username());
                    return new IllegalArgumentException(Messages.INVALID_USERNAME_OR_PASSWORD);
                });

        if (!user.getPassword().equals(requestDto.password())) {
            log.warn("Ошибка авторизации: неверный пароль для username={}", requestDto.username());
            throw new IllegalArgumentException(Messages.INVALID_USERNAME_OR_PASSWORD);
        }

        log.info("Пользователь успешно авторизован: id={}, username={}",
                user.getId(), user.getUsername());

        return userMapper.toResponseDto(user);
    }
}