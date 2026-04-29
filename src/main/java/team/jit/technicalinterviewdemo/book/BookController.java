package team.jit.technicalinterviewdemo.book;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import team.jit.technicalinterviewdemo.api.BookNotFoundException;
import team.jit.technicalinterviewdemo.api.DuplicateIsbnException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookRepository bookRepository;

    @GetMapping
    public List<Book> findAll() {
        return bookRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @GetMapping("/{id}")
    public Book findById(@PathVariable Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@Valid @RequestBody BookRequest request) {
        validateUniqueIsbn(request.isbn(), null);
        Book book = new Book(request.title(), request.author(), request.isbn(), request.publicationYear());
        Book savedBook = bookRepository.saveAndFlush(book);
        log.info("Created book id={} isbn={} title={}", savedBook.getId(), savedBook.getIsbn(), savedBook.getTitle());
        return savedBook;
    }

    @PutMapping("/{id}")
    public Book update(@PathVariable Long id, @Valid @RequestBody BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        validateUniqueIsbn(request.isbn(), id);
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setPublicationYear(request.publicationYear());

        Book updatedBook = bookRepository.saveAndFlush(book);
        log.info("Updated book id={} isbn={} title={}", updatedBook.getId(), updatedBook.getIsbn(), updatedBook.getTitle());
        return updatedBook;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }

        bookRepository.deleteById(id);
        log.info("Deleted book id={}", id);
    }

    private void validateUniqueIsbn(String isbn, Long currentBookId) {
        boolean exists = currentBookId == null
                ? bookRepository.existsByIsbn(isbn)
                : bookRepository.existsByIsbnAndIdNot(isbn, currentBookId);

        if (exists) {
            throw new DuplicateIsbnException(isbn);
        }
    }
}
