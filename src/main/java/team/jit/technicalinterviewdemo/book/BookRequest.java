package team.jit.technicalinterviewdemo.book;

public record BookRequest(
        String title,
        String author,
        String isbn,
        Integer publicationYear
) {
}
