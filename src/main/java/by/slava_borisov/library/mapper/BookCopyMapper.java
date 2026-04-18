package by.slava_borisov.library.mapper;

import by.slava_borisov.library.dto.request.BookCopyCreateRequestDto;
import by.slava_borisov.library.dto.request.BookCopyUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookCopyResponseDto;
import by.slava_borisov.library.model.BookCopy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface BookCopyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    BookCopy toEntity(BookCopyCreateRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    void updateEntityFromDto(BookCopyUpdateRequestDto dto, @MappingTarget BookCopy bookCopy);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    BookCopyResponseDto toResponseDto(BookCopy bookCopy);

    List<BookCopyResponseDto> toResponseDtoList(List<BookCopy> bookCopies);
}