package team.jit.technicalinterviewdemo.business.localization;

public class DuplicateLocalizationMessageException extends RuntimeException {

    public DuplicateLocalizationMessageException(String messageKey, String language) {
        super(
                "Localization message with key '%s' and language '%s' already exists."
                        .formatted(messageKey, language)
        );
    }
}
