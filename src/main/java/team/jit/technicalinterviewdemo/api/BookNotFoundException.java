package team.jit.technicalinterviewdemo.api;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("Book with id %d was not found.".formatted(id));
    }
}
