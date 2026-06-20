package by.slava_borisov.library.dto.response;

public record JwtAuthResponseDto(
        String tokenType,
        String accessToken,
        UserResponseDto user
) {
}