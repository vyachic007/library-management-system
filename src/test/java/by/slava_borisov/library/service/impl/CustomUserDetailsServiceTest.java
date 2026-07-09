package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.model.Role;
import by.slava_borisov.library.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;


    @Test
    @DisplayName("Должен загрузить активного пользователя для Spring Security")
    void loadUserByUsername_shouldReturnUserDetails_whenUserExistsAndActive() {
        Role roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("ROLE_USER");

        User user = new User();
        user.setId(15L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setIsActive(true);
        user.setRoles(Set.of(roleUser));

        Mockito.when(userDao.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("testuser");

        assertEquals("testuser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.isEnabled());
        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER"))
        );

        Mockito.verify(userDao).findByUsername("testuser");
    }


    @Test
    @DisplayName("Должен загрузить администратора для Spring Security")
    void loadUserByUsername_shouldReturnUserDetails_whenUserHasAdminRole() {
        Role roleAdmin = new Role();
        roleAdmin.setId(2L);
        roleAdmin.setName("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword("encodedAdminPassword");
        user.setIsActive(true);
        user.setRoles(Set.of(roleAdmin));

        Mockito.when(userDao.findByUsername("admin"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        assertEquals("admin", result.getUsername());
        assertEquals("encodedAdminPassword", result.getPassword());
        assertTrue(result.isEnabled());
        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
        );

        Mockito.verify(userDao).findByUsername("admin");
    }


    @Test
    @DisplayName("Должен вернуть disabled UserDetails, если пользователь неактивен")
    void loadUserByUsername_shouldReturnDisabledUserDetails_whenUserIsInactive() {
        Role roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName("ROLE_USER");

        User user = new User();
        user.setId(15L);
        user.setUsername("inactiveUser");
        user.setPassword("encodedPassword");
        user.setIsActive(false);
        user.setRoles(Set.of(roleUser));

        Mockito.when(userDao.findByUsername("inactiveUser"))
                .thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername("inactiveUser");

        assertEquals("inactiveUser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        assertFalse(result.isEnabled());

        Mockito.verify(userDao).findByUsername("inactiveUser");
    }


    @Test
    @DisplayName("Должен выбросить UsernameNotFoundException, если пользователь не найден")
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        Mockito.when(userDao.findByUsername("unknownUser"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknownUser")
        );

        Mockito.verify(userDao).findByUsername("unknownUser");
    }
}