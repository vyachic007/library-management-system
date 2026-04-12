package by.slava_borisov.library.dao.impl;

import by.slava_borisov.library.dao.AbstractDao;
import by.slava_borisov.library.dao.BorrowRecordDao;
import by.slava_borisov.library.model.BorrowRecord;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public class BorrowRecordDaoImpl extends AbstractDao<BorrowRecord, Long> implements BorrowRecordDao {

    public BorrowRecordDaoImpl() {
        super(BorrowRecord.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecord> findByUserId(Long userId) {
        return entityManager.createQuery(
                        "select br from BorrowRecord br where br.user.id = :userId",
                        BorrowRecord.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecord> findByBookCopyId(Long bookCopyId) {
        return entityManager.createQuery(
                        "select br from BorrowRecord br where br.bookCopy.id = :bookCopyId",
                        BorrowRecord.class)
                .setParameter("bookCopyId", bookCopyId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecord> findByStatus(String status) {
        return entityManager.createQuery(
                        "select br from BorrowRecord br where br.status = :status",
                        BorrowRecord.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecord> findActiveByUserId(Long userId) {
        return entityManager.createQuery(
                        "select br from BorrowRecord br " +
                                "where br.user.id = :userId and br.returnedAt is null",
                        BorrowRecord.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowRecord> findOverdueRecords(LocalDate currentDate) {
        return entityManager.createQuery(
                        "select br from BorrowRecord br " +
                                "where br.returnedAt is null and br.dueDate < :currentDate",
                        BorrowRecord.class)
                .setParameter("currentDate", currentDate)
                .getResultList();
    }
}