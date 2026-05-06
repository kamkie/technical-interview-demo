package team.jit.technicalinterviewdemo.business.book;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.technical.docs.OpenApiConfiguration;
import team.jit.technicalinterviewdemo.technical.security.SameSiteCsrfContract;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Public book reads plus authenticated write operations.")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(
            summary = "List books",
            description =
                    "Public endpoint with pagination, sorting, text filters, category filters, and publication-year"
                            + " filters.")
    public ResponseEntity<Page<BookResponse>> findAll(
            @ParameterObject BookSearchRequest request,
            @ParameterObject @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<BookResponse> payload = bookService.findAll(request, pageable).map(BookResponse::from);
        return ResponseEntity.ok(payload);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a book by id",
            description = "Public endpoint that returns a single book with its assigned categories.")
    public ResponseEntity<BookResponse> findById(@PathVariable Long id) {
        BookResponse payload = BookResponse.from(bookService.findById(id));
        return ResponseEntity.ok(payload);
    }

    @PostMapping
    @Operation(
            summary = "Create a book",
            description =
                    "Requires an authenticated session established through the configured OAuth provider login flow"
                            + " and a valid same-site CSRF header mirrored from the readable XSRF-TOKEN cookie.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME))
    @Parameter(
            name = SameSiteCsrfContract.HEADER_NAME,
            in = ParameterIn.HEADER,
            required = true,
            description = "Same-site CSRF header whose value must match the readable XSRF-TOKEN cookie.")
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookCreateRequest request) {
        BookResponse payload = BookResponse.from(bookService.create(request));
        return ResponseEntity.status(201).body(payload);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a book",
            description = "Requires an authenticated session, a valid same-site CSRF header mirrored from the readable"
                    + " XSRF-TOKEN cookie, and the current optimistic-lock version.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME))
    @Parameter(
            name = SameSiteCsrfContract.HEADER_NAME,
            in = ParameterIn.HEADER,
            required = true,
            description = "Same-site CSRF header whose value must match the readable XSRF-TOKEN cookie.")
    public ResponseEntity<BookResponse> update(@PathVariable Long id, @Valid @RequestBody BookUpdateRequest request) {
        BookResponse payload = BookResponse.from(bookService.update(id, request));
        return ResponseEntity.ok(payload);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a book",
            description =
                    "Requires an authenticated session and a valid same-site CSRF header mirrored from the readable"
                            + " XSRF-TOKEN cookie.",
            security = @SecurityRequirement(name = OpenApiConfiguration.SESSION_COOKIE_SCHEME))
    @Parameter(
            name = SameSiteCsrfContract.HEADER_NAME,
            in = ParameterIn.HEADER,
            required = true,
            description = "Same-site CSRF header whose value must match the readable XSRF-TOKEN cookie.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
