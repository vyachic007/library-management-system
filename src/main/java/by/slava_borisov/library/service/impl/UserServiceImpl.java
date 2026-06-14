package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.BorrowRecordDao;
import by.slava_borisov.library.dao.UserDao;
import by.slava_borisov.library.dto.request.UserUpdateRequestDto;
import by.slava_borisov.library.dto.response.BorrowRecordResponseDto;
import by.slava_borisov.library.dto.response.UserResponseDto;
import by.slava_borisov.library.exception.DuplicateException;
import by.slava_borisov.library.exception.InvalidCredentialsException;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.mapper.BorrowRecordMapper;
import by.slava_borisov.library.mapper.UserMapper;
import by.slava_borisov.library.model.BorrowRecord;
import by.slava_borisov.library.model.User;
import by.slava_borisov.library.service.UserService;
import by.slava_borisov.library.util.Messages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
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
        log.info("Получение пользователя по id={}", userId);

        User user = getUserEntityById(userId);

        log.info("Пользователь найден: id={}, username={}, email={}",
                user.getId(), user.getUsername(), user.getEmail());

        return userMapper.toResponseDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAll() {
        log.info("Получение списка всех пользователей");

        List<User> users = userDao.findAll();

        log.info("Получен список всех пользователей, количество={}", users.size());

        return userMapper.toResponseDtoList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllActiveUsers() {
        log.info("Получение списка активных пользователей");

        List<User> users = userDao.findAllActiveUsers();

        log.info("Получен список активных пользователей, количество={}", users.size());

        return userMapper.toResponseDtoList(users);
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(Long userId, UserUpdateRequestDto requestDto) {
        log.info("Обновление профиля пользователя: id={}, email={}", userId, requestDto.email());

        User user = getUserEntityById(userId);

        userDao.findByEmail(requestDto.email())
                .filter(existingUser -> !existingUser.getId().equals(userId))
                .ifPresent(existingUser -> {
                    log.warn("Попытка обновить профиль на уже занятый email: userId={}, email={}",
                            userId, requestDto.email());
                    throw new DuplicateException(Messages.USER_EMAIL_ALREADY_EXISTS);
                });

        user.setEmail(requestDto.email());
        user.setFirstName(requestDto.firstName());
        user.setLastName(requestDto.lastName());
        user.setPhone(requestDto.phone());

        User updatedUser = userDao.update(user);

        log.info("Профиль пользователя успешно обновлён: id={}, username={}, email={}",
                updatedUser.getId(), updatedUser.getUsername(), updatedUser.getEmail());

        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getCurrentBorrows(Long userId) {
        log.info("Получение текущих аренд пользователя: userId={}", userId);

        getUserEntityById(userId);
        List<BorrowRecord> borrowRecords = borrowRecordDao.findActiveByUserId(userId);

        log.info("Получены текущие аренды пользователя: userId={}, количество={}",
                userId, borrowRecords.size());

        return borrowRecordMapper.toResponseDtoList(borrowRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getBorrowHistory(Long userId) {
        log.info("Получение истории аренды пользователя: userId={}", userId);

        getUserEntityById(userId);
        List<BorrowRecord> borrowRecords = borrowRecordDao.findByUserId(userId);

        log.info("Получена история аренды пользователя: userId={}, количество={}",
                userId, borrowRecords.size());

        return borrowRecordMapper.toResponseDtoList(borrowRecords);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecordResponseDto> getOverdueBorrows(Long userId) {
        log.info("Получение просроченных аренд пользователя: userId={}", userId);

        getUserEntityById(userId);

        List<BorrowRecord> overdueRecords = borrowRecordDao.findOverdueRecords(LocalDate.now()).stream()
                .filter(borrowRecord -> borrowRecord.getUser().getId().equals(userId))
                .toList();

        log.info("Получены просроченные аренды пользователя: userId={}, количество={}",
                userId, overdueRecords.size());

        return borrowRecordMapper.toResponseDtoList(overdueRecords);
    }

    private User getUserEntityById(Long userId) {
        return userDao.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Пользователь не найден: id={}", userId);
                    return new NotFoundException(
                            Messages.USER_NOT_FOUND_BY_ID.formatted(userId)
                    );
                });
    }
}