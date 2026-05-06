package team.jit.technicalinterviewdemo.business.book;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import team.jit.technicalinterviewdemo.business.audit.AuditLogService;
import team.jit.technicalinterviewdemo.business.category.CategoryService;
import team.jit.technicalinterviewdemo.technical.api.InvalidRequestException;
import team.jit.technicalinterviewdemo.technical.metrics.ApplicationMetrics;

@ExtendWith(MockitoExtension.class)
class BookServiceTests {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private AuditLogService auditLogService;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookRepository, categoryService, mock(ApplicationMetrics.class), auditLogService);
    }

    @Test
    void findAllRejectsInvalidIsbnFilterCharacters() {
        BookSearchRequest request = new BookSearchRequest();
        request.setIsbn("isbn-abc");

        assertThatThrownBy(() -> bookService.findAll(request, PageRequest.of(0, 20))).isInstanceOf(InvalidRequestException.class).hasMessage("Filter 'isbn' may contain only digits, hyphens, and X.");

        verify(bookRepository, never()).findAll(
                org.mockito.ArgumentMatchers.<Specification<Book>>any(), org.mockito.ArgumentMatchers.any(Pageable.class)
        );
    }

    @Test
    void findAllRejectsReversedYearRange() {
        BookSearchRequest request = new BookSearchRequest();
        request.setYearFrom(2025);
        request.setYearTo(2024);

        assertThatThrownBy(() -> bookService.findAll(request, PageRequest.of(0, 20))).isInstanceOf(InvalidRequestException.class).hasMessage("'yearFrom' must be less than or equal to 'yearTo'.");

        verify(bookRepository, never()).findAll(
                org.mockito.ArgumentMatchers.<Specification<Book>>any(), org.mockito.ArgumentMatchers.any(Pageable.class)
        );
    }

    @Test
    void findAllRejectsTooManyCategoryFilters() {
        BookSearchRequest request = new BookSearchRequest();
        request.setCategory(List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));

        assertThatThrownBy(() -> bookService.findAll(request, PageRequest.of(0, 20))).isInstanceOf(InvalidRequestException.class).hasMessage("At most 10 category filters are supported.");

        verify(bookRepository, never()).findAll(
                org.mockito.ArgumentMatchers.<Specification<Book>>any(), org.mockito.ArgumentMatchers.any(Pageable.class)
        );
    }

    @Test
    void findAllRejectsUnsupportedSortAlias() {
        BookSearchRequest request = new BookSearchRequest();

        assertThatThrownBy(() -> bookService.findAll(
                request, PageRequest.of(0, 20, org.springframework.data.domain.Sort.by("unknown"))
        )).isInstanceOf(InvalidRequestException.class).hasMessage("Sort field 'unknown' is not supported. Use one of: id, title, author, isbn, year.");

        verify(bookRepository, never()).findAll(
                org.mockito.ArgumentMatchers.<Specification<Book>>any(), org.mockito.ArgumentMatchers.any(Pageable.class)
        );
    }

    @Test
    void findAllIgnoresBlankFilters() {
        BookSearchRequest request = new BookSearchRequest();
        request.setTitle("   ");
        request.setAuthor("\t");
        request.setIsbn("  ");
        request.setCategory(List.of(" ", "   "));
        when(bookRepository.findAll(
                org.mockito.ArgumentMatchers.<Specification<Book>>any(), org.mockito.ArgumentMatchers.any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of()));

        assertThatCode(() -> bookService.findAll(request, PageRequest.of(0, 20))).doesNotThrowAnyException();

        verify(bookRepository).findAll(
                org.mockito.ArgumentMatchers.<Specification<Book>>any(), org.mockito.ArgumentMatchers.any(Pageable.class)
        );
    }
}
