package team.jit.technicalinterviewdemo.business.localization.seed;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.jit.technicalinterviewdemo.business.localization.Localization;
import team.jit.technicalinterviewdemo.business.localization.LocalizationRepository;

@Slf4j
@Configuration
public class LocalizationDataInitializer {

    @Bean
    CommandLineRunner seedLocalizations(LocalizationRepository localizationMessageRepository) {
        return args -> {
            for (Localization seedMessage : LocalizationSeedData.defaultMessages()) {
                if (localizationMessageRepository.existsByMessageKeyAndLanguage(seedMessage.getMessageKey(), seedMessage.getLanguage())) {
                    continue;
                }

                Localization savedMessage = localizationMessageRepository.save(seedMessage);
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

