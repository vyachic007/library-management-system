package by.slava_borisov.library.controller.rest;

import by.slava_borisov.library.dto.request.AuthorCreateRequestDto;
import by.slava_borisov.library.dto.request.AuthorUpdateRequestDto;
import by.slava_borisov.library.dto.response.AuthorResponseDto;
import by.slava_borisov.library.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponseDto createAuthor(
            @Valid @RequestBody AuthorCreateRequestDto request
    ) {
        return authorService.create(request);
    }

    @GetMapping
    public List<AuthorResponseDto> getAllAuthors() {
        return authorService.getAll();
    }

    @GetMapping("/search")
    public List<AuthorResponseDto> getAuthorsByLastName(
            @RequestParam String lastName
    ) {
        return authorService.getByLastName(lastName);
    }

    @GetMapping("/{authorId}")
    public AuthorResponseDto getAuthorById(
            @PathVariable("authorId") Long authorId
    ) {
        return authorService.getById(authorId);
    }

    @PutMapping("/{authorId}")
    public AuthorResponseDto updateAuthor(
            @PathVariable("authorId") Long authorId,
            @Valid @RequestBody AuthorUpdateRequestDto request
    ) {
        return authorService.update(authorId, request);
    }

    @DeleteMapping("/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAuthorById(
            @PathVariable("authorId") Long authorId
    ) {
        authorService.delete(authorId);
    }
}
