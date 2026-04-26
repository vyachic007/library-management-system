package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.AuthorDao;
import by.slava_borisov.library.dto.request.AuthorCreateRequestDto;
import by.slava_borisov.library.dto.request.AuthorUpdateRequestDto;
import by.slava_borisov.library.dto.response.AuthorResponseDto;
import by.slava_borisov.library.mapper.AuthorMapper;
import by.slava_borisov.library.model.Author;
import by.slava_borisov.library.service.AuthorService;
import by.slava_borisov.library.util.Messages;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorDao authorDao;
    private final AuthorMapper authorMapper;

    @Override
    @Transactional
    public AuthorResponseDto create(AuthorCreateRequestDto requestDto) {
        if (authorDao.existsByFirstNameAndLastName(requestDto.firstName(), requestDto.lastName())) {
            throw new IllegalArgumentException(Messages.AUTHOR_ALREADY_EXISTS);
        }

        Author author = authorMapper.toEntity(requestDto);
        Author savedAuthor = authorDao.save(author);

        return authorMapper.toResponseDto(savedAuthor);
    }

    @Override
    @Transactional
    public AuthorResponseDto update(
            Long authorId,
            AuthorUpdateRequestDto requestDto
    ) {
        Author author = getAuthorEntityById(authorId);

        authorDao.findByFirstNameAndLastName(requestDto.firstName(), requestDto.lastName())
                .filter(existingAuthor -> !existingAuthor.getId().equals(authorId))
                .ifPresent(existingAuthor -> {
                    throw new IllegalArgumentException(Messages.AUTHOR_ALREADY_EXISTS);
                });

        authorMapper.updateEntityFromDto(requestDto, author);

        Author updatedAuthor = authorDao.update(author);
        return authorMapper.toResponseDto(updatedAuthor);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponseDto getById(Long authorId) {
        Author author = getAuthorEntityById(authorId);
        return authorMapper.toResponseDto(author);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponseDto> getAll() {
        return authorMapper.toResponseDtoList(authorDao.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponseDto> getByLastName(String lastName) {
        return authorMapper.toResponseDtoList(authorDao.findByLastName(lastName));
    }

    @Override
    @Transactional
    public void delete(Long authorId) {
        Author author = getAuthorEntityById(authorId);
        authorDao.delete(author);
    }

    private Author getAuthorEntityById(Long authorId) {
        return authorDao.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException(
                        Messages.AUTHOR_NOT_FOUND_BY_ID.formatted(authorId)
                ));
    }
}