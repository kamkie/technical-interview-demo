package team.jit.technicalinterviewdemo.business.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookUpdateRequest(
        @NotBlank(message = "title is required")
        String title,
        @NotBlank(message = "author is required")
        String author,
        @NotNull(message = "version is required")
        Long version,
        @NotNull(message = "publicationYear is required")
        Integer publicationYear,
        List<String> categories
) {
}
