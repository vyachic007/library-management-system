package by.slava_borisov.library.service;

import by.slava_borisov.library.dto.request.BorrowBookRequestDto;
import by.slava_borisov.library.dto.request.ExtendBorrowRequestDto;
import by.slava_borisov.library.dto.request.ReturnBookRequestDto;
import by.slava_borisov.library.dto.response.BorrowRecordResponseDto;

import java.util.List;

public interface BorrowRecordService {

    BorrowRecordResponseDto borrowBook(BorrowBookRequestDto requestDto);

    BorrowRecordResponseDto returnBook(ReturnBookRequestDto requestDto);

    BorrowRecordResponseDto extendBorrowPeriod(Long borrowRecordId, ExtendBorrowRequestDto requestDto);

    BorrowRecordResponseDto getById(Long borrowRecordId);

    List<BorrowRecordResponseDto> getAll();

    List<BorrowRecordResponseDto> getCurrentBorrows();

    List<BorrowRecordResponseDto> getOverdueBorrows();

    List<BorrowRecordResponseDto> getByUserId(Long userId);

    List<BorrowRecordResponseDto> getCurrentBorrowsByUserId(Long userId);

    List<BorrowRecordResponseDto> getBorrowHistoryByUserId(Long userId);

    List<BorrowRecordResponseDto> getOverdueBorrowsByUserId(Long userId);

    List<BorrowRecordResponseDto> getByBookCopyId(Long bookCopyId);

    List<BorrowRecordResponseDto> getBorrowHistoryByBookCopyId(Long bookCopyId);
}