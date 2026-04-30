package team.jit.technicalinterviewdemo.technical.testing;

import team.jit.technicalinterviewdemo.business.localization.LocalizationMessage;
import team.jit.technicalinterviewdemo.business.localization.LocalizationMessageRepository;
import team.jit.technicalinterviewdemo.technical.localization.LocalizationMessageSeedData;

public final class LocalizationMessageTestData {

    private LocalizationMessageTestData() {
    }

    public static DefaultLocalizationMessages reloadDefaultMessages(LocalizationMessageRepository localizationMessageRepository) {
        localizationMessageRepository.deleteAll();
        localizationMessageRepository.saveAll(LocalizationMessageSeedData.defaultMessages());
        return new DefaultLocalizationMessages(
                localizationMessageRepository.findByMessageKeyAndLanguage("error.book.not_found", "en").orElseThrow(),
                localizationMessageRepository.findByMessageKeyAndLanguage("error.book.not_found", "es").orElseThrow(),
                localizationMessageRepository.findByMessageKeyAndLanguage("error.request.invalid", "en").orElseThrow()
        );
    }

    public record DefaultLocalizationMessages(
            LocalizationMessage bookNotFoundEn,
            LocalizationMessage bookNotFoundEs,
            LocalizationMessage invalidRequestEn
    ) {
    }
}
