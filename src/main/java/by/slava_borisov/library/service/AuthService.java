package by.slava_borisov.library.service;

import by.slava_borisov.library.dto.request.UserLoginRequestDto;
import by.slava_borisov.library.dto.request.UserRegistrationRequestDto;
import by.slava_borisov.library.dto.response.JwtAuthResponseDto;
import by.slava_borisov.library.dto.response.UserResponseDto;

public interface AuthService {

    JwtAuthResponseDto register(UserRegistrationRequestDto requestDto);

    JwtAuthResponseDto login(UserLoginRequestDto requestDto);
}