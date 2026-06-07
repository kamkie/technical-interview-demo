package team.jit.technicalinterviewdemo.business.localization.seed;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationRepository;
import team.jit.technicalinterviewdemo.technical.bootstrap.BootstrapSettingsProperties;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class LocalizationDataInitializer {

    @Bean
    CommandLineRunner seedLocalizations(
            LocalizationRepository localizationMessageRepository,
            BootstrapSettingsProperties bootstrapSettingsProperties) {
        return args -> {
            if (!bootstrapSettingsProperties.getSeed().isDemoData()) {
                log.info("Skipping demo localization bootstrap because app.bootstrap.seed.demo-data is disabled.");
                return;
            }

            List<Localization> seedMessages = LocalizationSeedData.defaultMessages();
            Set<MessageIdentity> existingMessageIdentities =
                    existingMessageIdentities(localizationMessageRepository, seedMessages);
            List<Localization> missingMessages = seedMessages.stream()
                    .filter(seedMessage -> !existingMessageIdentities.contains(messageIdentity(seedMessage)))
                    .toList();
            if (missingMessages.isEmpty()) {
                return;
            }

            localizationMessageRepository
                    .saveAll(missingMessages)
                    .forEach(LocalizationDataInitializer::logSavedMessage);
        };
    }

    private static Set<MessageIdentity> existingMessageIdentities(
            LocalizationRepository localizationMessageRepository, List<Localization> seedMessages) {
        Set<String> messageKeys =
                seedMessages.stream().map(Localization::getMessageKey).collect(Collectors.toUnmodifiableSet());
        Set<String> languages =
                seedMessages.stream().map(Localization::getLanguage).collect(Collectors.toUnmodifiableSet());

        return localizationMessageRepository.findAllByMessageKeyInAndLanguageIn(messageKeys, languages).stream()
                .map(LocalizationDataInitializer::messageIdentity)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static MessageIdentity messageIdentity(Localization message) {
        return new MessageIdentity(message.getMessageKey(), message.getLanguage());
    }

    private static void logSavedMessage(Localization savedMessage) {
        log.info(
                "Seeded localization message id={} key={} language={}",
                savedMessage.getId(),
                savedMessage.getMessageKey(),
                savedMessage.getLanguage());
    }

    private record MessageIdentity(String messageKey, String language) {}
}
