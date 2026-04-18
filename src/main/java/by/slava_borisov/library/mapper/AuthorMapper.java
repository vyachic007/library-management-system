package by.slava_borisov.library.mapper;

import by.slava_borisov.library.dto.request.AuthorCreateRequestDto;
import by.slava_borisov.library.dto.request.AuthorUpdateRequestDto;
import by.slava_borisov.library.dto.response.AuthorResponseDto;
import by.slava_borisov.library.model.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AuthorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    Author toEntity(AuthorCreateRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true)
    void updateEntityFromDto(AuthorUpdateRequestDto dto, @MappingTarget Author author);

    AuthorResponseDto toResponseDto(Author author);

    List<AuthorResponseDto> toResponseDtoList(List<Author> authors);
}