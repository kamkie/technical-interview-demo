package team.jit.technicalinterviewdemo.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BookRequest(
        @NotBlank(message = "title is required")
        String title,
        @NotBlank(message = "author is required")
        String author,
        @NotBlank(message = "isbn is required")
        String isbn,
        @NotNull(message = "publicationYear is required")
        @Positive(message = "publicationYear must be positive")
        Integer publicationYear
) {
}
