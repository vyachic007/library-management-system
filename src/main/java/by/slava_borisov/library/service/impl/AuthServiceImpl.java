package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.RoleDao;
import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.dto.request.UserLoginRequestDto;
import by.slava_borisov.library.dto.request.UserRegistrationRequestDto;
import by.slava_borisov.library.dto.response.JwtAuthResponseDto;
import by.slava_borisov.library.exception.DuplicateException;
import by.slava_borisov.library.exception.InvalidCredentialsException;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.mapper.UserMapper;
import by.slava_borisov.library.model.Role;
import by.slava_borisov.library.model.User;
import by.slava_borisov.library.security.JwtTokenProvider;
import by.slava_borisov.library.service.AuthService;
import by.slava_borisov.library.util.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public JwtAuthResponseDto register(UserRegistrationRequestDto requestDto) {
        log.info("Регистрация пользователя: username={}, email={}",
                requestDto.username(), requestDto.email());

        if (userDao.existsByUsername(requestDto.username())) {
            log.warn("Попытка регистрации с уже занятым username={}", requestDto.username());
            throw new DuplicateException(Messages.USERNAME_ALREADY_EXISTS);
        }

        if (userDao.existsByEmail(requestDto.email())) {
            log.warn("Попытка регистрации с уже занятым email={}", requestDto.email());
            throw new DuplicateException(Messages.EMAIL_ALREADY_EXISTS);
        }

        Role userRole = roleDao.findByName(DEFAULT_USER_ROLE)
                .orElseThrow(() -> {
                    log.error("Роль по умолчанию не найдена: roleName={}", DEFAULT_USER_ROLE);
                    return new NotFoundException(
                            Messages.ROLE_NOT_FOUND_BY_NAME.formatted(DEFAULT_USER_ROLE)
                    );
                });

        User user = userMapper.toEntity(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setRoles(Set.of(userRole));
        user.setIsActive(true);

        User savedUser = userDao.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getUsername());
        String token = jwtTokenProvider.generateToken(userDetails, savedUser.getId());

        log.info("Пользователь успешно зарегистрирован: id={}, username={}, email={}",
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());

        return new JwtAuthResponseDto(
                "Bearer",
                token,
                userMapper.toResponseDto(savedUser)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public JwtAuthResponseDto login(UserLoginRequestDto requestDto) {
        log.info("Попытка авторизации пользователя: username={}", requestDto.username());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestDto.username(),
                            requestDto.password()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            User user = userDao.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> {
                        log.warn("Ошибка авторизации: пользователь не найден, username={}", requestDto.username());
                        return new InvalidCredentialsException(Messages.INVALID_USERNAME_OR_PASSWORD);
                    });

            String token = jwtTokenProvider.generateToken(userDetails, user.getId());

            log.info("Пользователь успешно авторизован: id={}, username={}",
                    user.getId(), user.getUsername());

            return new JwtAuthResponseDto(
                    "Bearer",
                    token,
                    userMapper.toResponseDto(user)
            );
        } catch (AuthenticationException ex) {
            log.warn("Ошибка авторизации: неверный username или password, username={}", requestDto.username());
            throw new InvalidCredentialsException(Messages.INVALID_USERNAME_OR_PASSWORD);
        }
    }
}