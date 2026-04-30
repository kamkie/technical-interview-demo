package team.jit.technicalinterviewdemo.localization;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocalizationMessageService {

    private final LocalizationMessageRepository localizationMessageRepository;

    public String getMessage(String messageKey, String language) {
        return findMessage(normalizeMessageKey(messageKey), normalizeLanguage(language))
                .map(LocalizationMessage::getMessageText)
                .orElseThrow(() -> new LocalizationMessageNotFoundException(messageKey, language));
    }

    public String getMessageWithFallback(String messageKey, String language, String fallbackLanguage) {
        String normalizedMessageKey = normalizeMessageKey(messageKey);
        String normalizedLanguage = normalizeLanguage(language);
        String normalizedFallbackLanguage = normalizeLanguage(fallbackLanguage);

        Optional<LocalizationMessage> requestedMessage = findMessage(normalizedMessageKey, normalizedLanguage);
        if (requestedMessage.isPresent()) {
            return requestedMessage.get().getMessageText();
        }

        return findMessage(normalizedMessageKey, normalizedFallbackLanguage)
                .map(LocalizationMessage::getMessageText)
                .orElseThrow(() -> new LocalizationMessageNotFoundException(messageKey, language, fallbackLanguage));
    }

    public Map<String, String> getAllMessages(String language) {
        Map<String, String> messagesByKey = new LinkedHashMap<>();
        for (LocalizationMessage message : localizationMessageRepository.findAllByLanguageOrderByMessageKeyAsc(normalizeLanguage(language))) {
            messagesByKey.put(message.getMessageKey(), message.getMessageText());
        }
        return messagesByKey;
    }

    private Optional<LocalizationMessage> findMessage(String messageKey, String language) {
        return localizationMessageRepository.findByMessageKeyAndLanguage(messageKey, language);
    }

    private String normalizeMessageKey(String messageKey) {
        if (messageKey == null || messageKey.isBlank()) {
            throw new IllegalArgumentException("messageKey is required");
        }
        return messageKey.trim();
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            throw new IllegalArgumentException("language is required");
        }
        return language.trim().toLowerCase(Locale.ROOT);
    }
}
