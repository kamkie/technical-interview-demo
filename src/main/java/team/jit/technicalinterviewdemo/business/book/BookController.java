package team.jit.technicalinterviewdemo.business.book;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.technical.docs.OpenApiConfiguration;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Public book reads plus authenticated write operations.")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(
            summary = "List books",
            description = "Public endpoint with pagination, sorting, text filters, category filters, and publication-year filters."
    )
    public ResponseEntity<Page<Book>> findAll(
            @ParameterObject BookSearchRequest request,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        Page<Book> payload = bookService.findAll(request, pageable);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by id", description = "Public endpoint that returns a single book with its assigned categories.")
    public ResponseEntity<Book> findById(@PathVariable Long id) {
        Book payload = bookService.findById(id);
        return ResponseEntity.ok(payload);
    }

    @PostMapping
    @Operation(
            summary = "Create a book",
            description = "Requires an authenticated session established through the GitHub OAuth login flow.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<Book> create(@Valid @RequestBody BookCreateRequest request) {
        Book payload = bookService.create(request);
        return ResponseEntity.status(201).body(payload);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a book",
            description = "Requires an authenticated session and the current optimistic-lock version.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<Book> update(@PathVariable Long id, @Valid @RequestBody BookUpdateRequest request) {
        Book payload = bookService.update(id, request);
        return ResponseEntity.ok(payload);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a book",
            description = "Requires an authenticated session.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME)
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
