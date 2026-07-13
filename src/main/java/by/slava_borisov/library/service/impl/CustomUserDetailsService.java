package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.model.Role;
import by.slava_borisov.library.model.User;
import by.slava_borisov.library.util.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Загрузка пользователя для Spring Security: username={}", username);

        User user = userDao.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Пользователь для Spring Security не найден: username={}", username);
                    return new UsernameNotFoundException(Messages.INVALID_USERNAME_OR_PASSWORD);
                });

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(
                        user.getRoles()
                                .stream()
                                .map(Role::getName)
                                .map(SimpleGrantedAuthority::new)
                                .toList()
                )
                .disabled(!Boolean.TRUE.equals(user.getIsActive()))
                .build();
    }
}
