package team.jit.technicalinterviewdemo.business.localization;

public class LocalizationNotFoundException extends RuntimeException {

    public LocalizationNotFoundException(Long id) {
        super("Localization with id %d was not found.".formatted(id));
    }

    public LocalizationNotFoundException(String messageKey, String language) {
        super("Localization with key '%s' and language '%s' was not found.".formatted(messageKey, language));
    }

    public LocalizationNotFoundException(String messageKey, String language, String fallbackLanguage) {
        super(
                "Localization with key '%s' was not found for language '%s' or fallback language '%s'.".formatted(messageKey, language, fallbackLanguage)
        );
    }
}
