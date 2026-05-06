package team.jit.technicalinterviewdemo.business.book;

public class DuplicateIsbnException extends RuntimeException {

    public DuplicateIsbnException(String isbn) {
        super("Book with ISBN %s already exists.".formatted(isbn));
    }
}
