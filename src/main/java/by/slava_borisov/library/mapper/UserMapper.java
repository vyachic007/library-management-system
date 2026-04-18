package by.slava_borisov.library.mapper;

import by.slava_borisov.library.dto.request.UserRegistrationRequestDto;
import by.slava_borisov.library.dto.response.UserResponseDto;
import by.slava_borisov.library.model.Role;
import by.slava_borisov.library.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "borrowRecords", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    User toEntity(UserRegistrationRequestDto userRegistrationRequestDto);

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserResponseDto toResponseDto(User user);

    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}