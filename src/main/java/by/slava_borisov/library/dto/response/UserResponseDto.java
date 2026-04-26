package by.slava_borisov.library.dto.response;

import java.util.Set;

public record UserResponseDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String phone,
        Boolean isActive,
        Set<String> roles
) {
}