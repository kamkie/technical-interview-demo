package team.jit.technicalinterviewdemo.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookRequest(
        @NotBlank(message = "title is required")
        String title,
        @NotBlank(message = "author is required")
        String author,
        @NotBlank(message = "isbn is required")
        String isbn,
        @NotNull(message = "publicationYear is required")
        Integer publicationYear
) {
}
