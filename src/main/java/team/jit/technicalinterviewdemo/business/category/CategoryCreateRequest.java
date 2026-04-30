package team.jit.technicalinterviewdemo.business.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryCreateRequest(
        @NotBlank(message = "name is required")
        String name
) {
}
