package by.slava_borisov.library.service;

import by.slava_borisov.library.dto.request.UserUpdateRequestDto;
import by.slava_borisov.library.dto.response.BorrowRecordResponseDto;
import by.slava_borisov.library.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {

    UserResponseDto getById(Long userId);

    List<UserResponseDto> getAll();

    List<UserResponseDto> getAllActiveUsers();

    UserResponseDto updateProfile(Long userId, UserUpdateRequestDto requestDto);

    List<BorrowRecordResponseDto> getCurrentBorrows(Long userId);

    List<BorrowRecordResponseDto> getBorrowHistory(Long userId);

    List<BorrowRecordResponseDto> getOverdueBorrows(Long userId);
}