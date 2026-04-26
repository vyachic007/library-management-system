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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorDao authorDao;
    private final AuthorMapper authorMapper;

    @Override
    @Transactional
    public AuthorResponseDto create(AuthorCreateRequestDto requestDto) {
        log.info("Создание автора: имя={}, фамилия={}", requestDto.firstName(), requestDto.lastName());

        if (authorDao.existsByFirstNameAndLastName(requestDto.firstName(), requestDto.lastName())) {
            log.warn("Попытка создать уже существующего автора: имя={}, фамилия={}",
                    requestDto.firstName(), requestDto.lastName());
            throw new IllegalArgumentException(Messages.AUTHOR_ALREADY_EXISTS);
        }

        Author author = authorMapper.toEntity(requestDto);
        Author savedAuthor = authorDao.save(author);

        log.info("Автор успешно создан: id={}, имя={}, фамилия={}",
                savedAuthor.getId(), savedAuthor.getFirstName(), savedAuthor.getLastName());

        return authorMapper.toResponseDto(savedAuthor);
    }

    @Override
    @Transactional
    public AuthorResponseDto update(
            Long authorId,
            AuthorUpdateRequestDto requestDto
    ) {
        log.info("Обновление автора: id={}, новое имя={}, новая фамилия={}",
                authorId, requestDto.firstName(), requestDto.lastName());

        Author author = getAuthorEntityById(authorId);

        authorDao.findByFirstNameAndLastName(requestDto.firstName(), requestDto.lastName())
                .filter(existingAuthor -> !existingAuthor.getId().equals(authorId))
                .ifPresent(existingAuthor -> {
                    log.warn("Попытка обновить автора на уже существующие данные: id={}, имя={}, фамилия={}",
                            authorId, requestDto.firstName(), requestDto.lastName());
                    throw new IllegalArgumentException(Messages.AUTHOR_ALREADY_EXISTS);
                });

        authorMapper.updateEntityFromDto(requestDto, author);

        Author updatedAuthor = authorDao.update(author);

        log.info("Автор успешно обновлён: id={}, имя={}, фамилия={}",
                updatedAuthor.getId(), updatedAuthor.getFirstName(), updatedAuthor.getLastName());

        return authorMapper.toResponseDto(updatedAuthor);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorResponseDto getById(Long authorId) {
        log.info("Получение автора по id={}", authorId);

        Author author = getAuthorEntityById(authorId);

        log.info("Автор найден: id={}, имя={}, фамилия={}",
                author.getId(), author.getFirstName(), author.getLastName());

        return authorMapper.toResponseDto(author);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponseDto> getAll() {
        log.info("Получение списка всех авторов");

        List<Author> authors = authorDao.findAll();

        log.info("Получен список авторов, количество={}", authors.size());

        return authorMapper.toResponseDtoList(authors);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuthorResponseDto> getByLastName(String lastName) {
        log.info("Поиск авторов по фамилии={}", lastName);

        List<Author> authors = authorDao.findByLastName(lastName);

        log.info("Поиск авторов по фамилии завершён: фамилия={}, найдено={}", lastName, authors.size());

        return authorMapper.toResponseDtoList(authors);
    }

    @Override
    @Transactional
    public void delete(Long authorId) {
        log.info("Удаление автора: id={}", authorId);

        Author author = getAuthorEntityById(authorId);
        authorDao.delete(author);

        log.info("Автор успешно удалён: id={}, имя={}, фамилия={}",
                author.getId(), author.getFirstName(), author.getLastName());
    }

    private Author getAuthorEntityById(Long authorId) {
        return authorDao.findById(authorId)
                .orElseThrow(() -> {
                    log.warn("Автор не найден: id={}", authorId);
                    return new EntityNotFoundException(
                            Messages.AUTHOR_NOT_FOUND_BY_ID.formatted(authorId)
                    );
                });
    }
}