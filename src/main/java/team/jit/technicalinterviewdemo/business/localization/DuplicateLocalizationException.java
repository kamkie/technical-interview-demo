package team.jit.technicalinterviewdemo.business.localization;

public class DuplicateLocalizationException extends RuntimeException {

    public DuplicateLocalizationException(String messageKey, String language) {
        super("Localization with key '%s' and language '%s' already exists.".formatted(messageKey, language));
    }
}
