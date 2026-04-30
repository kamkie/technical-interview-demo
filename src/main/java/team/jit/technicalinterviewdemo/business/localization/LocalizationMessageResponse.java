package team.jit.technicalinterviewdemo.business.localization;

import java.time.LocalDateTime;

public record LocalizationMessageResponse(
        Long id,
        String messageKey,
        String language,
        String messageText,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static LocalizationMessageResponse from(LocalizationMessage message) {
        return new LocalizationMessageResponse(
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
