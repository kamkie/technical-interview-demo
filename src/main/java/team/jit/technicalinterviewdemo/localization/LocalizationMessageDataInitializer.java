package team.jit.technicalinterviewdemo.localization;

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
            if (localizationMessageRepository.count() > 0) {
                return;
            }

            List<LocalizationMessage> seedMessages = List.of(
                    new LocalizationMessage(
                            "error.book.not_found",
                            "en",
                            "The requested book was not found.",
                            "English message for missing book errors."
                    ),
                    new LocalizationMessage(
                            "error.book.not_found",
                            "es",
                            "No se encontro el libro solicitado.",
                            "Spanish message for missing book errors."
                    ),
                    new LocalizationMessage(
                            "error.book.not_found",
                            "de",
                            "Das angeforderte Buch wurde nicht gefunden.",
                            "German message for missing book errors."
                    ),
                    new LocalizationMessage(
                            "error.request.invalid",
                            "en",
                            "The request is invalid.",
                            "English message for invalid request errors."
                    ),
                    new LocalizationMessage(
                            "error.request.invalid",
                            "es",
                            "La solicitud no es valida.",
                            "Spanish message for invalid request errors."
                    ),
                    new LocalizationMessage(
                            "error.request.invalid",
                            "de",
                            "Die Anfrage ist ungueltig.",
                            "German message for invalid request errors."
                    )
            );

            localizationMessageRepository.saveAll(seedMessages).forEach(message -> log.info(
                    "Seeded localization message id={} key={} language={}",
                    message.getId(),
                    message.getMessageKey(),
                    message.getLanguage()
            ));
        };
    }
}
