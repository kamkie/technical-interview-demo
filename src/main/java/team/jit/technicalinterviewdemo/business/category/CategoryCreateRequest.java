package team.jit.technicalinterviewdemo.business.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for creating a category.")
public record CategoryCreateRequest(
        @Schema(description = "Unique category name.", example = "Software Engineering")
        @NotBlank(message = "name is required")
        String name
) {
}
