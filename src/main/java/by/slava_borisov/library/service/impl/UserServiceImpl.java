package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.BorrowRecordDao;
import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.dto.request.UserUpdateRequestDto;
import by.slava_borisov.library.dto.response.BorrowRecordResponseDto;
import by.slava_borisov.library.dto.response.UserResponseDto;
import by.slava_borisov.library.mapper.BorrowRecordMapper;
import by.slava_borisov.library.mapper.UserMapper;
import by.slava_borisov.library.model.BorrowRecord;
import by.slava_borisov.library.model.User;
import by.slava_borisov.library.service.UserService;
import by.slava_borisov.library.util.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final BorrowRecordDao borrowRecordDao;
    private final UserMapper userMapper;
    private final BorrowRecordMapper borrowRecordMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getById(Long userId) {
        User user = getUserEntityById(userId);
        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        return userMapper.toResponseDtoList(userDao.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllActiveUsers() {
        return userMapper.toResponseDtoList(userDao.findAllActiveUsers());
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(Long userId, UserUpdateRequestDto requestDto) {
        User user = getUserEntityById(userId);

        userDao.findByEmail(requestDto.email())
                .filter(existingUser -> !existingUser.getId().equals(userId))
                .ifPresent(existingUser -> {
                    throw new IllegalArgumentException(Messages.USER_EMAIL_ALREADY_EXISTS);
                });

        user.setEmail(requestDto.email());
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setPhone(requestDto.phone());

        User updatedUser = userDao.update(user);
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getCurrentBorrows(Long userId) {
        getUserEntityById(userId);
        return borrowRecordMapper.toResponseDtoList(borrowRecordDao.findActiveByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getBorrowHistory(Long userId) {
        getUserEntityById(userId);
        return borrowRecordMapper.toResponseDtoList(borrowRecordDao.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getOverdueBorrows(Long userId) {
        getUserEntityById(userId);

        List<BorrowRecord> overdueRecords = borrowRecordDao.findOverdueRecords(LocalDate.now()).stream()
                .filter(borrowRecord -> borrowRecord.getUser().getId().equals(userId))
                .toList();

        return borrowRecordMapper.toResponseDtoList(overdueRecords);
    }

    private User getUserEntityById(Long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.USER_NOT_FOUND_BY_ID.formatted(userId)
                ));
    }
}