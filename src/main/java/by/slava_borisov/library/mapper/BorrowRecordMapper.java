package by.slava_borisov.library.mapper;

import by.slava_borisov.library.dto.request.BorrowBookRequestDto;
import by.slava_borisov.library.dto.response.BorrowRecordResponseDto;
import by.slava_borisov.library.model.BorrowRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface BorrowRecordMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "bookCopy", ignore = true)
    @Mapping(target = "returnedAt", ignore = true)
    @Mapping(target = "status", constant = "BORROWED")
    BorrowRecord toEntity(BorrowBookRequestDto dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "bookCopyId", source = "bookCopy.id")
    @Mapping(target = "inventoryNumber", source = "bookCopy.inventoryNumber")
    @Mapping(target = "bookTitle", source = "bookCopy.book.title")
    BorrowRecordResponseDto toResponseDto(BorrowRecord borrowRecord);

    List<BorrowRecordResponseDto> toResponseDtoList(List<BorrowRecord> borrowRecords);
}