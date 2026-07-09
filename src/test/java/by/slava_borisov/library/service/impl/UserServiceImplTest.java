package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.dto.response.UserResponseDto;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.mapper.UserMapper;
import by.slava_borisov.library.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserDao userDao;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Если пользователь есть в DAO, сервис возвращает UserResponseDto")
    void getById_ShouldReturn_WhenUserExists() {

        User user = createUser(
                15L,
                "username15",
                "Вячеслав",
                "Борисов",
                "user12345@mail.ru"
        );

        UserResponseDto userResponseDto = createUserDto(
                15L,
                "username15",
                "Вячеслав",
                "Борисов",
                "user12345@mail.ru"
        );

        when(userDao.findById(15L))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponseDto(user))
                .thenReturn(userResponseDto);

        UserResponseDto result = userService.getById(15L);

        assertEquals(userResponseDto, result);

        verify(userDao).findById(15L);
        verify(userMapper).toResponseDto(user);
        verifyNoMoreInteractions(userDao, userMapper);
    }

    @Test
    @DisplayName("Должен выбросить NotFoundException, если пользователь с таким ID не найден")
    void getById_ShouldThrowNotFoundException_WhenUserNotExists() {

        when(userDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> userService.getById(99L)
        );

        verify(userDao).findById(99L);
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("Должен вернуть список всех пользователей")
    void getAll_ShouldReturnUsersList() {

        List<User> users = List.of(
                createUser(15L, "username15", "Вячеслав", "Борисов", "user12345@mail.ru"),
                createUser(16L, "username99", "Иван", "Иванов", "ivan12345@mail.ru")
        );

        List<UserResponseDto> expected = List.of(
                createUserDto(15L, "username15", "Вячеслав", "Борисов", "user12345@mail.ru"),
                createUserDto(16L, "username99", "Иван", "Иванов", "ivan12345@mail.ru")
        );

        when(userDao.findAll())
                .thenReturn(users);

        when(userMapper.toResponseDtoList(users))
                .thenReturn(expected);

        List<UserResponseDto> result = userService.getAll();

        assertEquals(expected, result);

        verify(userDao).findAll();
        verify(userMapper).toResponseDtoList(users);
        verifyNoMoreInteractions(userDao, userMapper);
    }

    private User createUser(
            Long id,
            String username,
            String firstName,
            String lastName,
            String email
    ) {
        User user = new User();

        user.setId(id);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone("12345678");
        user.setIsActive(true);

        return user;
    }

    private UserResponseDto createUserDto(
            Long id,
            String username,
            String firstName,
            String lastName,
            String email
    ) {
        return new UserResponseDto(
                id,
                username,
                email,
                firstName,
                lastName,
                "12345678",
                true,
                Set.of("USER")
        );
    }
}