package team.jit.technicalinterviewdemo.business.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookCreateRequest(
        @NotBlank(message = "title is required")
        String title,
        @NotBlank(message = "author is required")
        String author,
        @NotBlank(message = "isbn is required")
        String isbn,
        @NotNull(message = "publicationYear is required")
        Integer publicationYear,
        List<String> categories
) {
}
