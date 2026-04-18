package by.slava_borisov.library.mapper;

import by.slava_borisov.library.dto.request.BookCreateRequestDto;
import by.slava_borisov.library.dto.request.BookUpdateRequestDto;
import by.slava_borisov.library.dto.response.BookDetailsResponseDto;
import by.slava_borisov.library.dto.response.BookResponseDto;
import by.slava_borisov.library.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        uses = {CategoryMapper.class, AuthorMapper.class}
)
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    Book toEntity(BookCreateRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "bookCopies", ignore = true)
    void updateEntityFromDto(BookUpdateRequestDto dto, @MappingTarget Book book);

    BookResponseDto toResponseDto(Book book);

    BookDetailsResponseDto toDetailsResponseDto(Book book);

    List<BookResponseDto> toResponseDtoList(List<Book> books);
}