package team.jit.technicalinterviewdemo.testdata;

import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationRepository;
import team.jit.technicalinterviewdemo.business.localization.seed.LocalizationSeedData;

public final class LocalizationTestData {

    private LocalizationTestData() {}

    public static DefaultLocalizations reloadDefaultMessages(LocalizationRepository localizationMessageRepository) {
        localizationMessageRepository.deleteAll();
        localizationMessageRepository.saveAll(LocalizationSeedData.defaultMessages());
        return new DefaultLocalizations(
                localizationMessageRepository
                        .findByMessageKeyAndLanguage("error.book.not_found", "en")
                        .orElseThrow(),
                localizationMessageRepository
                        .findByMessageKeyAndLanguage("error.book.not_found", "es")
                        .orElseThrow(),
                localizationMessageRepository
                        .findByMessageKeyAndLanguage("error.request.invalid", "en")
                        .orElseThrow());
    }

    public record DefaultLocalizations(
            Localization bookNotFoundEn, Localization bookNotFoundEs, Localization invalidRequestEn) {}
}
