package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.UserLoginRequestDto;
import by.slava_borisov.library.dto.request.UserRegistrationRequestDto;
import by.slava_borisov.library.dto.response.JwtAuthResponseDto;
import by.slava_borisov.library.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Аутентификация",
        description = "Регистрация пользователей и вход в систему с получением JWT-токена"
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Вход в систему",
            description = "Проверяет логин и пароль пользователя и возвращает JWT-токен"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Вход выполнен успешно"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Переданы некорректные данные"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Неверный логин или пароль"
            )
    })
    @PostMapping("/login")
    public JwtAuthResponseDto login(
            @Valid @RequestBody UserLoginRequestDto request
    ) {
        return authService.login(request);
    }


    @Operation(
            summary = "Регистрация пользователя",
            description = "Создаёт нового пользователя и возвращает JWT-токен"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно зарегистрирован"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Переданы некорректные регистрационные данные"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь с таким логином уже существует"
            )
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register")
    public JwtAuthResponseDto register(
            @Valid @RequestBody UserRegistrationRequestDto request
    ) {
        return authService.register(request);
    }
}
