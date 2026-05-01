package team.jit.technicalinterviewdemo.business.localization;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Localization message returned by the API.")
public record LocalizationResponse(
        @Schema(description = "Database identifier.", example = "1")
        Long id,
        @Schema(description = "Stable message key.", example = "error.request.resource_not_found")
        String messageKey,
        @Schema(description = "Two-letter ISO 639-1 language code.", example = "en")
        String language,
        @Schema(description = "Localized message text.", example = "The requested resource was not found.")
        String messageText,
        @Schema(description = "Optional reviewer-facing description.")
        String description,
        @Schema(description = "Creation timestamp.")
        LocalDateTime createdAt,
        @Schema(description = "Last update timestamp.")
        LocalDateTime updatedAt
) {

    public static LocalizationResponse from(Localization message) {
        return new LocalizationResponse(
                message.getId(),
                message.getMessageKey(),
                message.getLanguage(),
                message.getMessageText(),
                message.getDescription(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
