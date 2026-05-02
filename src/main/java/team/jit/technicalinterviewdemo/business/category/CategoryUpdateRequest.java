package team.jit.technicalinterviewdemo.business.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Payload for updating a category.")
public record CategoryUpdateRequest(
        @Schema(description = "Unique category name.", example = "Software Architecture")
        @NotBlank(message = "name is required")
        String name
) {
}
