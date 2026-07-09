package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.exception.ForbiddenOperationException;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AccessControlServiceImplTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private AccessControlServiceImpl accessControlService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    @DisplayName("Должен вернуть ID текущего пользователя")
    void getCurrentUserId_shouldReturnCurrentUserId_whenUserExists() {
        setAuthentication("testuser", "ROLE_USER");

        User user = new User();
        user.setId(15L);
        user.setUsername("testuser");

        Mockito.when(userDao.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        Long result = accessControlService.getCurrentUserId();

        assertEquals(15L, result);

        Mockito.verify(userDao).findByUsername("testuser");
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException, если текущий пользователь не найден")
    void getCurrentUserId_shouldThrowException_whenUserNotFound() {
        setAuthentication("unknownUser", "ROLE_USER");

        Mockito.when(userDao.findByUsername("unknownUser"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> accessControlService.getCurrentUserId()
        );

        Mockito.verify(userDao).findByUsername("unknownUser");
    }


    @Test
    @DisplayName("Должен вернуть true, если текущий пользователь администратор")
    void isCurrentUserAdmin_shouldReturnTrue_whenUserHasAdminRole() {
        setAuthentication("admin", "ROLE_ADMIN");

        boolean result = accessControlService.isCurrentUserAdmin();

        assertTrue(result);

        verifyNoInteractions(userDao);
    }


    @Test
    @DisplayName("Должен вернуть false, если текущий пользователь не администратор")
    void isCurrentUserAdmin_shouldReturnFalse_whenUserHasNoAdminRole() {
        setAuthentication("testuser", "ROLE_USER");

        boolean result = accessControlService.isCurrentUserAdmin();

        assertFalse(result);

        verifyNoInteractions(userDao);
    }


    @Test
    @DisplayName("Должен разрешить доступ, если текущий пользователь администратор")
    void checkUserAccess_shouldAllowAccess_whenCurrentUserIsAdmin() {
        setAuthentication("admin", "ROLE_ADMIN");

        assertDoesNotThrow(
                () -> accessControlService.checkUserAccess(99L)
        );

        verifyNoInteractions(userDao);
    }


    @Test
    @DisplayName("Должен разрешить доступ, если пользователь обращается к своим данным")
    void checkUserAccess_shouldAllowAccess_whenCurrentUserAccessesOwnData() {
        setAuthentication("testuser", "ROLE_USER");

        User user = new User();
        user.setId(15L);
        user.setUsername("testuser");

        Mockito.when(userDao.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        assertDoesNotThrow(
                () -> accessControlService.checkUserAccess(15L)
        );

        Mockito.verify(userDao).findByUsername("testuser");
    }


    @Test
    @DisplayName("Должен выбросить ForbiddenOperationException, если пользователь обращается к чужим данным")
    void checkUserAccess_shouldThrowException_whenCurrentUserAccessesAnotherUserData() {
        setAuthentication("testuser", "ROLE_USER");

        User user = new User();
        user.setId(15L);
        user.setUsername("testuser");

        Mockito.when(userDao.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        assertThrows(
                ForbiddenOperationException.class,
                () -> accessControlService.checkUserAccess(99L)
        );

        Mockito.verify(userDao).findByUsername("testuser");
    }


    @Test
    @DisplayName("Должен выбросить NotFoundException при проверке доступа, если текущий пользователь не найден")
    void checkUserAccess_shouldThrowException_whenCurrentUserNotFound() {
        setAuthentication("unknownUser", "ROLE_USER");

        Mockito.when(userDao.findByUsername("unknownUser"))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> accessControlService.checkUserAccess(99L)
        );

        Mockito.verify(userDao).findByUsername("unknownUser");
    }


    private void setAuthentication(String username, String role) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        List.of(new SimpleGrantedAuthority(role))
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}