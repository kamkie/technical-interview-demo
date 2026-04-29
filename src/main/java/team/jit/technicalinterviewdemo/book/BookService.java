package team.jit.technicalinterviewdemo.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import team.jit.technicalinterviewdemo.api.BookNotFoundException;
import team.jit.technicalinterviewdemo.api.DuplicateIsbnException;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> findAll() {
        return bookRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(BookRequest request) {
        validateUniqueIsbn(request.isbn(), null);
        Book book = new Book(request.title(), request.author(), request.isbn(), request.publicationYear());
        Book savedBook = bookRepository.saveAndFlush(book);
        log.info("Created book id={} isbn={} title={}", savedBook.getId(), savedBook.getIsbn(), savedBook.getTitle());
        return savedBook;
    }

    public Book update(Long id, BookRequest request) {
        Book book = findById(id);

        validateUniqueIsbn(request.isbn(), id);
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setIsbn(request.isbn());
        book.setPublicationYear(request.publicationYear());

        Book updatedBook = bookRepository.saveAndFlush(book);
        log.info("Updated book id={} isbn={} title={}", updatedBook.getId(), updatedBook.getIsbn(), updatedBook.getTitle());
        return updatedBook;
    }

    public void delete(Long id) {
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
