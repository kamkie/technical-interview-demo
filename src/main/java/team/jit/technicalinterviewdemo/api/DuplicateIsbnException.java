package team.jit.technicalinterviewdemo.api;

public class DuplicateIsbnException extends RuntimeException {

    public DuplicateIsbnException(String isbn) {
        super("Book with ISBN %s already exists.".formatted(isbn));
    }
}
