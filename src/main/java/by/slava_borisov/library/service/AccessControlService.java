package by.slava_borisov.library.service;

public interface AccessControlService {

    Long getCurrentUserId();

    boolean isCurrentUserAdmin();

    void checkUserAccess(Long userId);
}