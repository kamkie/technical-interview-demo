package team.jit.technicalinterviewdemo.localization;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.jit.technicalinterviewdemo.api.InvalidRequestException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocalizationMessageService {

    private static final Pattern MESSAGE_KEY_PATTERN = Pattern.compile("^[a-z0-9._-]+$");
    private static final Pattern LANGUAGE_PATTERN = Pattern.compile("^[a-zA-Z]{2}$");
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "messageKey", "language", "createdAt", "updatedAt");

    private final LocalizationMessageRepository localizationMessageRepository;

    public Page<LocalizationMessage> findAll(Pageable pageable) {
        Pageable effectivePageable = createEffectivePageable(pageable);
        return localizationMessageRepository.findAll(effectivePageable);
    }

    public LocalizationMessage findById(Long id) {
        return localizationMessageRepository.findById(id)
                .orElseThrow(() -> new LocalizationMessageNotFoundException(id));
    }

    public LocalizationMessage findByMessageKeyAndLanguage(String messageKey, String language) {
        return findMessage(normalizeMessageKey(messageKey), normalizeLanguage(language))
                .orElseThrow(() -> new LocalizationMessageNotFoundException(messageKey, language));
    }

    public String getMessage(String messageKey, String language) {
        return findByMessageKeyAndLanguage(messageKey, language).getMessageText();
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

    public List<LocalizationMessage> findAllByLanguage(String language) {
        return localizationMessageRepository.findAllByLanguageOrderByMessageKeyAsc(normalizeLanguage(language));
    }

    @Transactional
    public LocalizationMessage create(LocalizationMessageRequest request) {
        String messageKey = normalizeMessageKey(request.messageKey());
        String language = normalizeLanguage(request.language());
        validateUniqueMessage(messageKey, language, null);

        LocalizationMessage message = new LocalizationMessage(
                messageKey,
                language,
                request.messageText(),
                request.description()
        );
        LocalizationMessage savedMessage = localizationMessageRepository.saveAndFlush(message);
        log.info(
                "Created localization message id={} key={} language={}",
                savedMessage.getId(),
                savedMessage.getMessageKey(),
                savedMessage.getLanguage()
        );
        return savedMessage;
    }

    @Transactional
    public LocalizationMessage update(Long id, LocalizationMessageRequest request) {
        LocalizationMessage message = findById(id);
        String messageKey = normalizeMessageKey(request.messageKey());
        String language = normalizeLanguage(request.language());
        validateUniqueMessage(messageKey, language, id);

        message.setMessageKey(messageKey);
        message.setLanguage(language);
        message.setMessageText(request.messageText());
        message.setDescription(request.description());

        LocalizationMessage updatedMessage = localizationMessageRepository.saveAndFlush(message);
        log.info(
                "Updated localization message id={} key={} language={}",
                updatedMessage.getId(),
                updatedMessage.getMessageKey(),
                updatedMessage.getLanguage()
        );
        return updatedMessage;
    }

    @Transactional
    public void delete(Long id) {
        if (!localizationMessageRepository.existsById(id)) {
            throw new LocalizationMessageNotFoundException(id);
        }
        localizationMessageRepository.deleteById(id);
        log.info("Deleted localization message id={}", id);
    }

    private Optional<LocalizationMessage> findMessage(String messageKey, String language) {
        return localizationMessageRepository.findByMessageKeyAndLanguage(messageKey, language);
    }

    private String normalizeMessageKey(String messageKey) {
        if (messageKey == null || messageKey.isBlank()) {
            throw new InvalidRequestException("messageKey is required.");
        }

        String normalizedMessageKey = messageKey.trim();
        if (!MESSAGE_KEY_PATTERN.matcher(normalizedMessageKey).matches()) {
            throw new InvalidRequestException("messageKey must match ^[a-z0-9._-]+$.");
        }
        return normalizedMessageKey;
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            throw new InvalidRequestException("language is required.");
        }

        String normalizedLanguage = language.trim().toLowerCase(Locale.ROOT);
        if (!LANGUAGE_PATTERN.matcher(normalizedLanguage).matches()) {
            throw new InvalidRequestException("language must be a two-letter ISO 639-1 code.");
        }
        return normalizedLanguage;
    }

    private Pageable createEffectivePageable(Pageable pageable) {
        Sort effectiveSort = pageable.getSort().isSorted() ? normalizeSort(pageable.getSort()) : Sort.by(Sort.Order.asc("id"));
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), effectiveSort);
    }

    private Sort normalizeSort(Sort sort) {
        for (Sort.Order order : sort) {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new InvalidRequestException(
                        "Sort field '%s' is not supported. Use one of: id, messageKey, language, createdAt, updatedAt."
                                .formatted(order.getProperty())
                );
            }
        }
        return sort;
    }

    private void validateUniqueMessage(String messageKey, String language, Long id) {
        boolean exists = id == null
                ? localizationMessageRepository.existsByMessageKeyAndLanguage(messageKey, language)
                : localizationMessageRepository.existsByMessageKeyAndLanguageAndIdNot(messageKey, language, id);
        if (exists) {
            throw new DuplicateLocalizationMessageException(messageKey, language);
        }
    }
}
