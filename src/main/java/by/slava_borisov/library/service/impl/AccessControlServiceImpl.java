package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.exception.ForbiddenOperationException;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.model.User;
import by.slava_borisov.library.service.AccessControlService;
import by.slava_borisov.library.util.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccessControlServiceImpl implements AccessControlService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserDao userDao;

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(Messages.USER_NOT_FOUND));

        return user.getId();
    }

    @Override
    public boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> ROLE_ADMIN.equals(authority.getAuthority()));
    }

    @Override
    public void checkUserAccess(Long userId) {
        if (isCurrentUserAdmin()) {
            return;
        }

        Long currentUserId = getCurrentUserId();

        if (!currentUserId.equals(userId)) {
            throw new ForbiddenOperationException(Messages.ACCESS_DENIED);
        }
    }
}