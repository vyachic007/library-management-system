package by.slava_borisov.library.service.impl;

import by.slava_borisov.library.dao.AuthorDao;
import by.slava_borisov.library.dto.request.AuthorCreateRequestDto;
import by.slava_borisov.library.dto.request.AuthorUpdateRequestDto;
import by.slava_borisov.library.dto.response.AuthorResponseDto;
import by.slava_borisov.library.exception.NotFoundException;
import by.slava_borisov.library.mapper.AuthorMapper;
import by.slava_borisov.library.model.Author;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorDao authorDao;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Test
    @DisplayName("Если автор есть в DAO, сервис возвращает AuthorResponseDto")
    void getById_shouldReturnAuthor_whenAuthorExists() {
        Author author = new Author();
        author.setId(15L);
        author.setFirstName("Вячеслав");
        author.setLastName("Борисов");

        AuthorResponseDto authorResponseDto = new AuthorResponseDto(
                15L,
                "Вячеслав",
                "Борисов"
        );

        Mockito.when(authorDao.findById(15L))
                .thenReturn(Optional.of(author));

        Mockito.when(authorMapper.toResponseDto(author))
                .thenReturn(authorResponseDto);

        AuthorResponseDto result = authorService.getById(15L);

        assertEquals(15L, result.id());
        assertEquals("Вячеслав", result.firstName());
        assertEquals("Борисов", result.lastName());

        Mockito.verify(authorDao).findById(15L);
        Mockito.verify(authorMapper).toResponseDto(author);
    }

    @Test
    @DisplayName("Должен выбросить NotFoundException, если автор с таким ID не найден")
    void getById_shouldThrowException_whenAuthorNotFound() {
        Mockito.when(authorDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> authorService.getById(99L)
        );

        Mockito.verify(authorDao).findById(99L);
        verifyNoInteractions(authorMapper);
    }

    @Test
    @DisplayName("Должен вернуть список всех авторов")
    void getAll_shouldReturnAllAuthors() {
        Author author1 = new Author();
        author1.setId(1L);
        author1.setFirstName("Иван");
        author1.setLastName("Иванов");

        Author author2 = new Author();
        author2.setId(2L);
        author2.setFirstName("Антон");
        author2.setLastName("Антонов");

        List<Author> authors = List.of(author1, author2);

        AuthorResponseDto responseDto1 = new AuthorResponseDto(
                1L,
                "Иван",
                "Иванов"
        );

        AuthorResponseDto responseDto2 = new AuthorResponseDto(
                2L,
                "Антон",
                "Антонов"
        );

        List<AuthorResponseDto> responseDtos = List.of(responseDto1, responseDto2);

        Mockito.when(authorDao.findAll())
                .thenReturn(authors);

        Mockito.when(authorMapper.toResponseDtoList(authors))
                .thenReturn(responseDtos);

        List<AuthorResponseDto> result = authorService.getAll();

        assertEquals(1L, result.get(0).id());
        assertEquals("Иван", result.get(0).firstName());
        assertEquals("Иванов", result.get(0).lastName());

        assertEquals(2L, result.get(1).id());
        assertEquals("Антон", result.get(1).firstName());
        assertEquals("Антонов", result.get(1).lastName());

        Mockito.verify(authorDao).findAll();
        Mockito.verify(authorMapper).toResponseDtoList(authors);
    }

    @Test
    @DisplayName("Должен вернуть список авторов по фамилии")
    void getByLastName_shouldReturnAuthors() {
        Author author1 = new Author();
        author1.setId(1L);
        author1.setFirstName("Иван");
        author1.setLastName("Иванов");

        Author author2 = new Author();
        author2.setId(2L);
        author2.setFirstName("Антон");
        author2.setLastName("Иванов");

        List<Author> authors = List.of(author1, author2);

        AuthorResponseDto responseDto1 = new AuthorResponseDto(
                1L,
                "Иван",
                "Иванов"
        );

        AuthorResponseDto responseDto2 = new AuthorResponseDto(
                2L,
                "Антон",
                "Иванов"
        );

        List<AuthorResponseDto> responseDtos = List.of(responseDto1, responseDto2);

        Mockito.when(authorDao.findByLastName("Иванов"))
                .thenReturn(authors);

        Mockito.when(authorMapper.toResponseDtoList(authors))
                .thenReturn(responseDtos);

        List<AuthorResponseDto> result = authorService.getByLastName("Иванов");

        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).id());
        assertEquals("Иван", result.get(0).firstName());
        assertEquals("Иванов", result.get(0).lastName());

        assertEquals(2L, result.get(1).id());
        assertEquals("Антон", result.get(1).firstName());
        assertEquals("Иванов", result.get(1).lastName());

        Mockito.verify(authorDao).findByLastName("Иванов");
        Mockito.verify(authorMapper).toResponseDtoList(authors);
    }

    @Test
    @DisplayName("Должен успешно создать автора")
    void create_shouldCreateAuthor() {
        AuthorCreateRequestDto request = new AuthorCreateRequestDto(
                "Иван",
                "Иванов"
        );

        Author authorToSave = new Author();
        authorToSave.setId(1L);
        authorToSave.setFirstName("Иван");
        authorToSave.setLastName("Иванов");

        Author savedAuthor = new Author();
        savedAuthor.setId(1L);
        savedAuthor.setFirstName("Иван");
        savedAuthor.setLastName("Иванов");

        AuthorResponseDto responseDto = new AuthorResponseDto(
                1L,
                "Иван",
                "Иванов"
        );

        Mockito.when(authorMapper.toEntity(request))
                .thenReturn(authorToSave);

        Mockito.when(authorDao.save(authorToSave))
                .thenReturn(savedAuthor);

        Mockito.when(authorMapper.toResponseDto(savedAuthor))
                .thenReturn(responseDto);

        AuthorResponseDto result = authorService.create(request);

        assertEquals(1L, result.id());
        assertEquals("Иван", result.firstName());
        assertEquals("Иванов", result.lastName());

        Mockito.verify(authorMapper).toEntity(request);
        Mockito.verify(authorDao).save(authorToSave);
        Mockito.verify(authorMapper).toResponseDto(savedAuthor);
    }

    @Test
    @DisplayName("Должен обновить автора, если автор существует")
    void update_shouldUpdateAuthor_whenAuthorExists() {
        AuthorUpdateRequestDto request = new AuthorUpdateRequestDto(
                "Иван",
                "Петров"
        );

        Author authorToUpdate = new Author();
        authorToUpdate.setId(15L);
        authorToUpdate.setFirstName("Иван");
        authorToUpdate.setLastName("Иванов");

        Author updatedAuthor = new Author();
        updatedAuthor.setId(15L);
        updatedAuthor.setFirstName("Иван");
        updatedAuthor.setLastName("Петров");

        AuthorResponseDto response = new AuthorResponseDto(
                15L,
                "Иван",
                "Петров"
        );

        Mockito.when(authorDao.findById(15L))
                .thenReturn(Optional.of(authorToUpdate));

        Mockito.when(authorDao.update(authorToUpdate))
                .thenReturn(updatedAuthor);

        Mockito.when(authorMapper.toResponseDto(updatedAuthor))
                .thenReturn(response);

        AuthorResponseDto result = authorService.update(15L, request);

        assertEquals(15L, result.id());
        assertEquals("Иван", result.firstName());
        assertEquals("Петров", result.lastName());

        Mockito.verify(authorDao).findById(15L);
        Mockito.verify(authorDao).update(authorToUpdate);
        Mockito.verify(authorMapper).toResponseDto(updatedAuthor);
    }

    @Test
    @DisplayName("Должен выбросить NotFoundException при обновлении, если автор не найден")
    void update_shouldThrowException_whenAuthorNotFound() {
        AuthorUpdateRequestDto request = new AuthorUpdateRequestDto(
                "Иван",
                "Иванов"
        );

        Mockito.when(authorDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> authorService.update(99L, request)
        );

        Mockito.verify(authorDao).findById(99L);
        Mockito.verify(authorDao, Mockito.never()).update(Mockito.any(Author.class));
        Mockito.verifyNoInteractions(authorMapper);
    }

    @Test
    @DisplayName("Должен удалить автора, если она существует")
    void delete_shouldDeleteAuthor_whenAuthorExists() {
        Author author = new Author();
        author.setId(16L);
        author.setFirstName("Вячеслав");
        author.setLastName("Борисов");

        Mockito.when(authorDao.findById(16L))
                .thenReturn(Optional.of(author));

        authorService.delete(16L);

        Mockito.verify(authorDao).findById(16L);
        Mockito.verify(authorDao).delete(author);
    }

    @Test
    @DisplayName("Должен выбросить NotFoundException при удалении, если автор не найден")
    void delete_shouldThrowException_whenAuthorNotFound() {
        Mockito.when(authorDao.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> authorService.delete(99L)
        );

        Mockito.verify(authorDao).findById(99L);
        Mockito.verify(authorDao, Mockito.never()).delete(Mockito.any(Author.class));
        Mockito.verify(authorDao, Mockito.never()).deleteById(Mockito.anyLong());
    }
}