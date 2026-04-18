package by.slava_borisov.library.mapper;

import by.slava_borisov.library.dto.request.CategoryCreateRequestDto;
import by.slava_borisov.library.dto.request.CategoryUpdateRequestDto;
import by.slava_borisov.library.dto.response.CategoryResponseDto;
import by.slava_borisov.library.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "children", ignore = true)
    Category toEntity(CategoryCreateRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "children", ignore = true)
    void updateEntityFromDto(CategoryUpdateRequestDto dto, @MappingTarget Category category);

    @Mapping(target = "parentId", source = "parent.id")
    CategoryResponseDto toResponseDto(Category category);

    List<CategoryResponseDto> toResponseDtoList(List<Category> categories);
}