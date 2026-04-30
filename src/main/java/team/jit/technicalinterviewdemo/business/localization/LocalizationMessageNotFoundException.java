package team.jit.technicalinterviewdemo.business.localization;

public class LocalizationMessageNotFoundException extends RuntimeException {

    public LocalizationMessageNotFoundException(Long id) {
        super("Localization message with id %d was not found.".formatted(id));
    }

    public LocalizationMessageNotFoundException(String messageKey, String language) {
        super("Localization message with key '%s' and language '%s' was not found.".formatted(messageKey, language));
    }

    public LocalizationMessageNotFoundException(String messageKey, String language, String fallbackLanguage) {
        super(
                "Localization message with key '%s' was not found for language '%s' or fallback language '%s'."
                        .formatted(messageKey, language, fallbackLanguage)
        );
    }
}
