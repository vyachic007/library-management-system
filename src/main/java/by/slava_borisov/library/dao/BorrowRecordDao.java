package by.slava_borisov.library.dao;

import by.slava_borisov.library.model.BorrowRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowRecordDao {

    Optional<BorrowRecord> findById(Long id);

    List<BorrowRecord> findAll();

    BorrowRecord save(BorrowRecord borrowRecord);

    BorrowRecord update(BorrowRecord borrowRecord);

    void delete(BorrowRecord borrowRecord);

    void deleteById(Long id);

    List<BorrowRecord> findByUserId(Long userId);

    List<BorrowRecord> findByBookCopyId(Long bookCopyId);

    List<BorrowRecord> findByStatus(String status);

    List<BorrowRecord> findActiveByUserId(Long userId);

    List<BorrowRecord> findOverdueRecords(LocalDate currentDate);
}