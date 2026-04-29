package team.jit.technicalinterviewdemo.book;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.api.BookNotFoundException;
import team.jit.technicalinterviewdemo.api.DuplicateIsbnException;
import team.jit.technicalinterviewdemo.api.StaleBookVersionException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public Page<Book> findAll(Pageable pageable) {
        Pageable effectivePageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "id"));
        return bookRepository.findAll(effectivePageable);
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    @Transactional
    public Book create(BookCreateRequest request) {
        validateUniqueIsbn(request.isbn());
        Book book = new Book(request.title(), request.author(), request.isbn(), request.publicationYear());
        Book savedBook = bookRepository.saveAndFlush(book);
        log.info("Created book id={} isbn={} title={}", savedBook.getId(), savedBook.getIsbn(), savedBook.getTitle());
        return savedBook;
    }

    @Transactional
    public Book update(Long id, BookUpdateRequest request) {
        Book book = findById(id);
        if (!book.getVersion().equals(request.version())) {
            throw new StaleBookVersionException(id, request.version(), book.getVersion());
        }

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublicationYear(request.publicationYear());

        Book updatedBook;
        try {
            updatedBook = bookRepository.saveAndFlush(book);
        } catch (ObjectOptimisticLockingFailureException exception) {
            throw new StaleBookVersionException(id, request.version(), book.getVersion(), exception);
        }
        log.info("Updated book id={} isbn={} title={}", updatedBook.getId(), updatedBook.getIsbn(), updatedBook.getTitle());
        return updatedBook;
    }

    @Transactional
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BookNotFoundException(id);
        }

        bookRepository.deleteById(id);
        log.info("Deleted book id={}", id);
    }

    private void validateUniqueIsbn(String isbn) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new DuplicateIsbnException(isbn);
        }
    }
}
