package team.jit.technicalinterviewdemo.business.localization;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LocalizationMessageRequest(
        @NotBlank(message = "messageKey is required")
        @Pattern(regexp = "^[a-z0-9._-]+$", message = "messageKey must match ^[a-z0-9._-]+$")
        @Size(max = 150, message = "messageKey must be at most 150 characters")
        String messageKey,
        @NotBlank(message = "language is required")
        @Pattern(regexp = "^[a-zA-Z]{2}$", message = "language must be a two-letter ISO 639-1 code")
        String language,
        @NotBlank(message = "messageText is required")
        @Size(max = 2000, message = "messageText must be at most 2000 characters")
        String messageText,
        @Size(max = 1000, message = "description must be at most 1000 characters")
        String description
) {
}
