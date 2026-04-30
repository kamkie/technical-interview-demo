package team.jit.technicalinterviewdemo.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank(message = "name is required")
        String name
) {
}
