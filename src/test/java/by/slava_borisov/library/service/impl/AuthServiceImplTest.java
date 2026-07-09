package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.RoleDao;
import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.dto.request.UserLoginRequestDto;
import by.slava_borisov.library.dto.request.UserRegistrationRequestDto;
import by.slava_borisov.library.dto.response.JwtAuthResponseDto;
import by.slava_borisov.library.dto.response.UserResponseDto;
import by.slava_borisov.library.exception.DuplicateException;
import by.slava_borisov.library.exception.InvalidCredentialsException;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.mapper.UserMapper;
import by.slava_borisov.library.model.Role;
import by.slava_borisov.library.model.User;
import by.slava_borisov.library.security.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserDao userDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;


    @Test
    @DisplayName("Должен зарегистрировать пользователя, если username и email свободны")
    void register_shouldRegisterUser_whenDataIsValid() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto(
                "testuser",
                "test12345",
                "testuser@example.com",
                "Вячеслав",
                "Борисов",
                "+79991234567"
        );

        Role userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");

        User userToSave = createUser(
                null,
                "testuser",
                "testuser@example.com",
                "Вячеслав",
                "Борисов",
                "+79991234567"
        );

        User savedUser = createUser(
                10L,
                "testuser",
                "testuser@example.com",
                "Вячеслав",
                "Борисов",
                "+79991234567"
        );
        savedUser.setPassword("encodedPassword");
        savedUser.setRoles(Set.of(userRole));
        savedUser.setIsActive(true);

        UserResponseDto userResponseDto = createUserResponseDto(
                10L,
                "testuser",
                "testuser@example.com",
                "Вячеслав",
                "Борисов",
                "+79991234567"
        );

        UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(userDao.existsByUsername("testuser"))
                .thenReturn(false);

        Mockito.when(userDao.existsByEmail("testuser@example.com"))
                .thenReturn(false);

        Mockito.when(roleDao.findByName("ROLE_USER"))
                .thenReturn(Optional.of(userRole));

        Mockito.when(userMapper.toEntity(request))
                .thenReturn(userToSave);

        Mockito.when(passwordEncoder.encode("test12345"))
                .thenReturn("encodedPassword");

        Mockito.when(userDao.save(userToSave))
                .thenReturn(savedUser);

        Mockito.when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);

        Mockito.when(jwtTokenProvider.generateToken(userDetails, 10L))
                .thenReturn("jwt-token");

        Mockito.when(userMapper.toResponseDto(savedUser))
                .thenReturn(userResponseDto);

        JwtAuthResponseDto result = authService.register(request);

        JwtAuthResponseDto expected = new JwtAuthResponseDto(
                "Bearer",
                "jwt-token",
                userResponseDto
        );

        assertEquals(expected, result);

        assertEquals("encodedPassword", userToSave.getPassword());
        assertEquals(Set.of(userRole), userToSave.getRoles());
        assertTrue(userToSave.getIsActive());

        Mockito.verify(userDao).existsByUsername("testuser");
        Mockito.verify(userDao).existsByEmail("testuser@example.com");
        Mockito.verify(roleDao).findByName("ROLE_USER");
        Mockito.verify(userMapper).toEntity(request);
        Mockito.verify(passwordEncoder).encode("test12345");
        Mockito.verify(userDao).save(userToSave);
        Mockito.verify(userDetailsService).loadUserByUsername("testuser");
        Mockito.verify(jwtTokenProvider).generateToken(userDetails, 10L);
        Mockito.verify(userMapper).toResponseDto(savedUser);
    }


    @Test
    @DisplayName("Должен выбросить DuplicateException при регистрации, если username уже занят")
    void register_shouldThrowException_whenUsernameAlreadyExists() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto(
                "testuser",
                "test12345",
                "testuser@example.com",
                "Вячеслав",
                "Борисов",
                "+79991234567"
        );

        Mockito.when(userDao.existsByUsername("testuser"))
                .thenReturn(true);

        assertThrows(
                DuplicateException.class,
                () -> authService.register(request)
        );

        Mockito.verify(userDao).existsByUsername("testuser");
        Mockito.verify(userDao, Mockito.never()).existsByEmail(Mockito.anyString());
        verifyNoInteractions(roleDao);
        verifyNoInteractions(userMapper);
        verifyNoInteractions(passwordEncoder);
        Mockito.verify(userDao, Mockito.never()).save(Mockito.any(User.class));
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(userDetailsService);
    }


    @Test
    @DisplayName("Должен выбросить DuplicateException при регистрации, если email уже занят")
    void register_shouldThrowException_whenEmailAlreadyExists() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto(
                "testuser",
                "test12345",
                "testuser@example.com",
                "Вячеслав",
                "Борисов",
                "+79991234567"
        );

        Mockito.when(userDao.existsByUsername("testuser"))
                .thenReturn(false);

        Mockito.when(userDao.existsByEmail("testuser@example.com"))
                .thenReturn(true);

        assertThrows(
                DuplicateException.class,
                () -> authService.register(request)
        );

        Mockito.verify(userDao).existsByUsername("testuser");
        Mockito.verify(userDao).existsByEmail("testuser@example.com");
        verifyNoInteractions(roleDao);
        verifyNoInteractions(userMapper);
        verifyNoInteractions(passwordEncoder);
        Mockito.verify(userDao, Mockito.never()).save(Mockito.any(User.class));
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(userDetailsService);
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при регистрации, если роль по умолчанию не найдена")
    void register_shouldThrowException_whenDefaultRoleNotFound() {
        UserRegistrationRequestDto request = new UserRegistrationRequestDto(
                "testuser",
                "test12345",
                "testuser@example.com",
                "Вячеслав",
                "Борисов",
                "+79991234567"
        );

        Mockito.when(userDao.existsByUsername("testuser"))
                .thenReturn(false);

        Mockito.when(userDao.existsByEmail("testuser@example.com"))
                .thenReturn(false);

        Mockito.when(roleDao.findByName("ROLE_USER"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> authService.register(request)
        );

        Mockito.verify(userDao).existsByUsername("testuser");
        Mockito.verify(userDao).existsByEmail("testuser@example.com");
        Mockito.verify(roleDao).findByName("ROLE_USER");
        verifyNoInteractions(userMapper);
        verifyNoInteractions(passwordEncoder);
        Mockito.verify(userDao, Mockito.never()).save(Mockito.any(User.class));
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(userDetailsService);
    }


    @Test
    @DisplayName("Должен авторизовать пользователя и вернуть JWT")
    void login_shouldReturnJwtAuthResponse_whenCredentialsAreValid() {
        UserLoginRequestDto request = new UserLoginRequestDto(
                "testuser",
                "test12345"
        );

        User user = createUser(
                10L,
                "testuser",
                "testuser@example.com",
                "Вячеслав",
                "Борисов",
                "+79991234567"
        );

        UserResponseDto userResponseDto = createUserResponseDto(
                10L,
                "testuser",
                "testuser@example.com",
                "Вячеслав",
                "Борисов",
                "+79991234567"
        );

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        Mockito.when(authentication.getPrincipal())
                .thenReturn(userDetails);

        Mockito.when(userDetails.getUsername())
                .thenReturn("testuser");

        Mockito.when(userDao.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        Mockito.when(jwtTokenProvider.generateToken(userDetails, 10L))
                .thenReturn("jwt-token");

        Mockito.when(userMapper.toResponseDto(user))
                .thenReturn(userResponseDto);

        JwtAuthResponseDto result = authService.login(request);

        JwtAuthResponseDto expected = new JwtAuthResponseDto(
                "Bearer",
                "jwt-token",
                userResponseDto
        );

        assertEquals(expected, result);

        Mockito.verify(authenticationManager)
                .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));

        Mockito.verify(authentication).getPrincipal();
        Mockito.verify(userDetails).getUsername();
        Mockito.verify(userDao).findByUsername("testuser");
        Mockito.verify(jwtTokenProvider).generateToken(userDetails, 10L);
        Mockito.verify(userMapper).toResponseDto(user);
    }


    @Test
    @DisplayName("Должен выбросить InvalidCredentialsException при логине, если пароль неверный")
    void login_shouldThrowException_whenAuthenticationFails() {
        UserLoginRequestDto request = new UserLoginRequestDto(
                "testuser",
                "wrongPassword"
        );

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        Mockito.verify(authenticationManager)
                .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));

        verifyNoInteractions(userDao);
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(userMapper);
    }


    @Test
    @DisplayName("Должен выбросить InvalidCredentialsException при логине, если пользователь не найден после аутентификации")
    void login_shouldThrowException_whenUserNotFoundAfterAuthentication() {
        UserLoginRequestDto request = new UserLoginRequestDto(
                "testuser",
                "test12345"
        );

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        Mockito.when(authentication.getPrincipal())
                .thenReturn(userDetails);

        Mockito.when(userDetails.getUsername())
                .thenReturn("testuser");

        Mockito.when(userDao.findByUsername("testuser"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.login(request)
        );

        Mockito.verify(authenticationManager)
                .authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class));

        Mockito.verify(authentication).getPrincipal();
        Mockito.verify(userDetails).getUsername();
        Mockito.verify(userDao).findByUsername("testuser");
        verifyNoInteractions(jwtTokenProvider);
        verifyNoInteractions(userMapper);
    }


    private User createUser(
            Long id,
            String username,
            String email,
            String firstName,
            String lastName,
            String phone
    ) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setIsActive(true);
        return user;
    }


    private UserResponseDto createUserResponseDto(
            Long id,
            String username,
            String email,
            String firstName,
            String lastName,
            String phone
    ) {
        return new UserResponseDto(
                id,
                username,
                email,
                firstName,
                lastName,
                phone,
                true,
                Set.of("ROLE_USER")
        );
    }
}