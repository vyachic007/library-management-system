package by.slava_borisov.library.service;

import by.slava_borisov.library.dto.request.AuthorCreateRequestDto;
import by.slava_borisov.library.dto.request.AuthorUpdateRequestDto;
import by.slava_borisov.library.dto.response.AuthorResponseDto;

import java.util.List;

public interface AuthorService {

    AuthorResponseDto create(AuthorCreateRequestDto requestDto);

    AuthorResponseDto update(Long authorId, AuthorUpdateRequestDto requestDto);

    AuthorResponseDto getById(Long authorId);

    List<AuthorResponseDto> getAll();

    List<AuthorResponseDto> getByLastName(String lastName);

    void delete(Long authorId);
}