package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.UserUpdateRequestDto;
import by.slava_borisov.library.dto.response.UserResponseDto;
import by.slava_borisov.library.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/profile")
    public UserResponseDto getProfile(
            @PathVariable Long userId
    ) {
        return userService.getProfile(userId);
    }

    @PutMapping("/{userId}/profile")
    public UserResponseDto updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequestDto request
    ) {
        return userService.updateProfile(userId, request);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(
            @PathVariable Long userId
    ) {
        return userService.getById(userId);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeUserById(
            @PathVariable Long userId
    ) {
        userService.delete(userId);
    }
}
