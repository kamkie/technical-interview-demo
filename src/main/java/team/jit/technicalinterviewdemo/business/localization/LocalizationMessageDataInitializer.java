package team.jit.technicalinterviewdemo.business.localization;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LocalizationMessageDataInitializer {

    @Bean
    CommandLineRunner seedLocalizationMessages(LocalizationMessageRepository localizationMessageRepository) {
        return args -> {
            for (LocalizationMessage seedMessage : LocalizationMessageSeedData.defaultMessages()) {
                if (localizationMessageRepository.existsByMessageKeyAndLanguage(seedMessage.getMessageKey(), seedMessage.getLanguage())) {
                    continue;
                }

                LocalizationMessage savedMessage = localizationMessageRepository.save(seedMessage);
                log.info(
                        "Seeded localization message id={} key={} language={}",
                        savedMessage.getId(),
                        savedMessage.getMessageKey(),
                        savedMessage.getLanguage()
                );
            }
        };
    }
}
