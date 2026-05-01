package team.jit.technicalinterviewdemo.business.localization;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for creating or updating a localization message.")
public record LocalizationRequest(
        @Schema(description = "Stable message key managed by the application.", example = "error.request.resource_not_found")
        @NotBlank(message = "messageKey is required")
        @Pattern(regexp = "^[a-z0-9._-]+$", message = "messageKey must match ^[a-z0-9._-]+$")
        @Size(max = 150, message = "messageKey must be at most 150 characters")
        String messageKey,
        @Schema(description = "Two-letter ISO 639-1 language code.", example = "pl")
        @NotBlank(message = "language is required")
        @Pattern(regexp = "^[a-zA-Z]{2}$", message = "language must be a two-letter ISO 639-1 code")
        String language,
        @Schema(description = "Localized message text.", example = "Nie znaleziono zasobu.")
        @NotBlank(message = "messageText is required")
        @Size(max = 2000, message = "messageText must be at most 2000 characters")
        String messageText,
        @Schema(description = "Optional reviewer-facing description.", example = "Displayed when a requested resource does not exist.")
        @Size(max = 1000, message = "description must be at most 1000 characters")
        String description
) {
}
